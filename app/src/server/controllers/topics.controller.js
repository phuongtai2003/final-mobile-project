require('dotenv').config();
const { Topic, Vocabulary } = require('../models');

const getTopicById = async (req, res) => {
    const id = req.params.id || req.query.id;
    try {
        const topic = await Topic.findById(id).populate('vocabularyId');
        res.status(200).json(topic);
    } catch (error) {
        res.status(500).json({ error : error.message });
        
    }
}

const getAllTopics = async (req, res) => {
    try {
        const topics = await Topic.find().populate('vocabularyId');
        if(topics.length === 0){
            res.status(404).json({error: "Topics not found"});
            return;
        }
        res.status(200).json({topics});
    } catch (error) {
        res.status(500).json({ error : error.message });
    }
}

const createTopic = async (req, res) => {
    const {topicNameEnglish, topicNameVietnamese, descriptionEnglish, descriptionVietnamese, isPublic} = req.body;
    const user = req.user;
    try {
        const topic = await Topic.create({topicNameEnglish, topicNameVietnamese, descriptionEnglish, descriptionVietnamese,
            isPublic, userId : user.data._id});
        res.status(200).json({topic});
    }catch(error){
        res.status(500).json({error : error.message});
    }
}

const updateTopic = async (req, res) => {
    const id = req.params.id || req.query.id;
    const {topicNameEnglish, topicNameVietnamese, descriptionEnglish, descriptionVietNamese} = req.body;
    try {
        const topic = await Topic.findByIdAndUpdate(id, {topicNameEnglish, topicNameVietnamese, descriptionEnglish, descriptionVietNamese}, {new: true});
        res.status(200).json({topic});
    } catch (error) {
        res.status(500).json({ error : error.message });
    }
}

const deleteTopic = async (req, res) => {
    const id = req.params.id || req.query.id;
    try {
        await Topic.findByIdAndDelete(id);
        res.status(200).json({ message: 'Delete topic successfully'});
    } catch (error) {
        res.status(500).json({ error : error.message });
    }
}

const addVocabularyToTopic = async (req, res) => {
    const topicId = req.params.id || req.query.id;
    const { englishWord, vietnameseWord, englishMeaning, vietnameseMeaning } = req.body;
    try {
        const vocabulary = await Vocabulary.create({ englishWord, vietnameseWord, englishMeaning, vietnameseMeaning });
        let topic = await Topic.findById(topicId);
        topic.vocabularyId.push(vocabulary._id);
        topic.vocabularyCount += 1;
        await topic.save();
        res.status(200).json({ message: 'Vocabulary added to topic successfully', topic});
    } catch (error) {
        res.status(500).json({ error : error.message });
        
    }
}

const deleteVocabularyInTopic = async (req, res) => {
    const topicId = req.params.id || req.query.id;
    const vocabularyId = req.params.vocabularyId;
    try {
        await Topic.findByIdAndUpdate(topicId, { $pull: { vocabularyId: vocabularyId }, $inc: { vocabularyCount: -1 } });
        res.status(200).json({ message: 'Vocabulary deleted from topic successfully' });
    } catch (error) {
        res.status(500).json({ error : error.message });
        
    }
}

const editVocabularyInTopic = async (req, res) => {
    const topicId = req.params.id || req.query.id;
    const vocabularyId = req.params.vocabularyId;
    const { englishWord, vietnameseWord, englishMeaning, vietnameseMeaning } = req.body;
    try {
        let topic = await Topic.findById(topicId);
        if (!topic.vocabularyId.includes(vocabularyId)) {
            return res.status(404).json({ error: 'Vocabulary does not exist in this topic' });
        }
        const vocabulary = await Vocabulary.findByIdAndUpdate(vocabularyId, { englishWord, vietnameseWord, englishMeaning, vietnameseMeaning }, { new: true });
        res.status(200).json({ message: 'Vocabulary edited successfully', vocabulary});
    } catch (error) {
        res.status(500).json({ error : error.message });
    }
}

const upVoteCount = async (req, res) => {
    const topicId = req.params.id || req.query.id;
    try {
        const topic = await Topic.findByIdAndUpdate(topicId, { $inc: { upVoteCount: 1 } }, { new: true });
        res.status(200).json({ message: 'Up vote successfully', topic});
    } catch (error) {
        res.status(500).json({ error : error.message });
    }
}

const downVoteCount = async (req, res) => {
    const topicId = req.params.id || req.query.id;
    try {
        const topic = await Topic.findByIdAndUpdate(topicId, { $inc: { downVoteCount: 1 } }, { new: true });
        res.status(200).json({ message: 'Down vote successfully', topic});
    } catch (error) {
        res.status(500).json({ error : error.message });
    }
}

const importCSV = async (req, res) => {
    const topicId = req.params.id || req.query.id;
    const jsonArray = req.body;
    try {
        let topic = await Topic.findById(topicId);
        for (let i = 0; i < jsonArray.length; i++) {
            const { englishWord, vietnameseWord, englishMeaning, vietnameseMeaning } = jsonArray[i];
            const vocabulary = await Vocabulary.create({ englishWord, vietnameseWord, englishMeaning, vietnameseMeaning });
            topic.vocabularyId.push(vocabulary._id);
            topic.vocabularyCount += 1;
        }
        await topic.save();
        res.status(200).json({ message: 'Import data successfully', topic});
    } catch (error) {
        res.status(500).json({ error : error.message });
    }
}

const exportCSV = async (req, res) => {
    const topicId = req.params.id || req.query.id;
    try {
        let topic = await Topic.findById(topicId).populate('vocabularyId');
        if (!topic) {
            return res.status(404).json({ error: 'Topic not found' });
        }
        const jsonArray = topic.vocabularyId.map(vocabulary => ({
            englishWord: vocabulary.englishWord,
            vietnameseWord: vocabulary.vietnameseWord,
            englishMeaning: vocabulary.englishMeaning,
            vietnameseMeaning: vocabulary.vietnameseMeaning
        }));
        res.status(200).json({ message: 'Export data successfully', data: jsonArray});
    } catch (error) {
        res.status(500).json({ error : error.message });
    }
}


module.exports = {
    getTopicById,
    getAllTopics,
    createTopic,
    updateTopic,
    deleteTopic,
    addVocabularyToTopic,
    deleteVocabularyInTopic,
    upVoteCount,
    downVoteCount,
    editVocabularyInTopic,
    importCSV,
    exportCSV
}