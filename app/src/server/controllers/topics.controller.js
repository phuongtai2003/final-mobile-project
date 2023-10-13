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
        const topic = await Topic.findByIdAndDelete(id);
        res.status(200).json({topic});
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

const deleteVocabularyFromTopic = async (req, res) => {
    const topicId = req.params.id || req.query.id;
    const vocabularyId = req.params.vocabularyId;
    try {
        await Topic.findByIdAndUpdate(topicId, { $pull: { vocabularyId: vocabularyId } });
        res.status(200).json({ message: 'Vocabulary deleted from topic successfully' });
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

module.exports = {
    getTopicById,
    getAllTopics,
    createTopic,
    updateTopic,
    deleteTopic,
    addVocabularyToTopic,
    deleteVocabularyFromTopic,
    upVoteCount,
    downVoteCount
}