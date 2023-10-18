require('dotenv').config();
const { Topic, Vocabulary, TopicInFolder, BookmarkVocabulary, VocabularyStatistic, LearningStatistics} = require('../models');

const getTopicById = async (req, res) => {
    const id = req.params.id || req.query.id;
    try {
        const topic = await Topic.findById(id);
        res.status(200).json({topic});
    } catch (error) {
        res.status(500).json({ error : error.message });
        
    }
    // const userId = req.user.data._id;
    // const topicId = req.params.id || req.query.id;

    // try {
    //     const vocabularies = await Vocabulary.find({ topicId: topicId });

    //     for (let i = 0; i < vocabularies.length; i++) {
    //         const vocabStat = await VocabularyStatistic.findOne({ userId, vocabularyId: vocabularies[i]._id });
    //         const bookmarkVocab = await BookmarkVocabulary.findOne({ userId, vocabularyId: vocabularies[i]._id });

    //         vocabularies[i] = vocabularies[i].toObject(); // Convert the Mongoose document to a plain JavaScript object
    //         vocabularies[i].vocabStat = vocabStat;
    //         vocabularies[i].bookmarkVocab = bookmarkVocab;
    //     }

    //     res.status(200).json({ vocabularies });
    // } catch (error) {
    //     res.status(500).json({ error: error.message });
    // }
}

const getAllTopics = async (req, res) => {
    try {
        const topics = await Topic.find().populate('vocabularyId');
        if(topics.length === 0){
            res.status(404).json({error: "Topics not found"});
            return;
        }
        res.status(200).json({topics, size : topics.length});
    } catch (error) {
        res.status(500).json({ error : error.message });
    }
}

const getTopicsByUserId = async (req, res) => {
    const userId = req.params.id || req.query.id;
    try {
        const topics = await Topic.find({ userId });
        if(topics.length === 0){
            res.status(404).json({error: "Topics not found"});
            return;
        }
        res.status(200).json({topics});
    } catch (error) {
        res.status(500).json({ error : error.message });
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
        await TopicInFolder.deleteMany({ topicId: id });
        await VocabularyStatistic.deleteMany({ topicId: id });
        await BookmarkVocabulary.deleteMany({ topicId: id });
        await LearningStatistics.deleteMany({ topicId: id });
        await Vocabulary.deleteMany({ topicId: id });
        await Topic.findByIdAndDelete(id);
        res.status(200).json({ message: 'Delete topic successfully'});
    } catch (error) {
        res.status(500).json({ error : error.message });
    }
}

const createVocabularyInTopic = async (req, res) => {
    const topicId = req.params.id || req.query.id;
    const { englishWord, vietnameseWord, englishMeaning, vietnameseMeaning } = req.body;
    try {
        let topic = await Topic.findByIdAndUpdate(topicId, {$inc: { vocabularyCount: 1 } }, { new: true });
        const vocabulary = await Vocabulary.create({ englishWord, vietnameseWord, englishMeaning, vietnameseMeaning, topicId });
        res.status(200).json({ message: 'Vocabulary added to topic successfully', topic, vocabulary});
    } catch (error) {
        res.status(500).json({ error : error.message });
    }
}

const deleteVocabularyInTopic = async (req, res) => {
    const topicId = req.params.id || req.query.id;
    const vocabularyId = req.params.vocabularyId;
    try {
        const vocab = await Vocabulary.findOne({ _id: vocabularyId, topicId });
        if (!vocab) {
            return res.status(404).json({ error: 'Vocabulary does not exist in topic' });
        }
        const topic = await Topic.findByIdAndUpdate(topicId, { $inc: { vocabularyCount: -1 } }, { new: true });
        await BookmarkVocabulary.findOneAndDelete({vocabularyId});
        await VocabularyStatistic.findOneAndDelete({vocabularyId});
        await Vocabulary.findByIdAndDelete(vocabularyId);
        res.status(200).json({ message: 'Vocabulary deleted successfully', topic});
    } catch (error) {
        res.status(500).json({ error : error.message });
        
    }
}

const editVocabularyInTopic = async (req, res) => {
    const topicId = req.params.id || req.query.id;
    const vocabularyId = req.params.vocabularyId;
    const { englishWord, vietnameseWord, englishMeaning, vietnameseMeaning } = req.body;
    try {
        const vocabulary = await Vocabulary.findOneAndUpdate({_id: vocabularyId, topicId}, { englishWord, vietnameseWord, englishMeaning, vietnameseMeaning }, { new: true });
        if(!vocabulary){
            return res.status(404).json({ error: 'Vocabulary does not exist in topic' });
        }
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
    const { topicNameEnglish, topicNameVietnamese, descriptionEnglish, descriptionVietnamese, vocabularyList, isPublic} = req.body;
    const userId = req.user.data._id;
    try {
        let topic = await Topic.create({
            topicNameEnglish,
            topicNameVietnamese,
            descriptionEnglish,
            descriptionVietnamese,
            vocabularyCount: vocabularyList.length,
            isPublic,
            userId
        });
        for (let i = 0; i < vocabularyList.length; i++) {
            const { englishWord, vietnameseWord, englishMeaning, vietnameseMeaning } = vocabularyList[i];
            await Vocabulary.create({ englishWord, vietnameseWord, englishMeaning, vietnameseMeaning, topicId : topic._id });
        }
        res.status(200).json({ message: 'Import data successfully', topic});
    } catch (error) {
        res.status(500).json({ error : error.message });
    }
}


const exportCSV = async (req, res) => {
    const topicId = req.params.id || req.query.id;
    try {
        const vocabularies = await Vocabulary.find({ topicId });
        const jsonArray = [];
        for (let i = 0; i < vocabularies.length; i++) {
            const { englishWord, vietnameseWord, englishMeaning, vietnameseMeaning } = vocabularies[i];
            jsonArray.push({ englishWord, vietnameseWord, englishMeaning, vietnameseMeaning });
        }
        res.status(200).json({ message: 'Export data successfully', data: jsonArray});
    } catch (error) {
        res.status(500).json({ error : error.message });
    }
}

const getTopicsByFolderId = async (req, res) => {
    const folderId = req.params.folderId || req.query.folderId;
    try {
        const topicsInFolder = await TopicInFolder.find({folderId}).populate('topicId');
        if(topicsInFolder.length === 0){
            res.status(404).json({error: "Topics not found"});
            return;
        }
        const topicsWithTimeAdded = topicsInFolder.map(topicInFolder => ({
            topic: topicInFolder.topicId,
            timeAdded: topicInFolder.dateTimeAdded
        }));
        res.status(200).json({topics: topicsWithTimeAdded});
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
}

const viewTopicIsPublic = async (req, res) => {
    const userId = req.params.userId || req.query.userId;
    try {
        const topicIsPublic = await Topic.find({userId, isPublic: true});
        if(topicIsPublic.length === 0){
            res.status(404).json({error: "Topics not found"});
            return;
        }
        res.status(200).json({topicIsPublic});
    } catch (error) {
        res.status(500).json({ error: error.message });
        
    }
}


module.exports = {
    getTopicById,
    getAllTopics,
    updateTopic,
    deleteTopic,
    createVocabularyInTopic,
    deleteVocabularyInTopic,
    upVoteCount,
    downVoteCount,
    editVocabularyInTopic,
    importCSV,
    exportCSV,
    getTopicsByUserId,
    getTopicsByFolderId,
    viewTopicIsPublic
}