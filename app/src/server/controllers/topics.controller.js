require('dotenv').config();
const { Topics } = require('../models');

const getTopicById = async (req, res) => {
    const id = req.params.id || req.query.id;
    try {
        const topic = await Topics.findById(id);
        res.status(200).json({topic});
    } catch (error) {
        res.status(500).json({ error });
        
    }
}

const getAllTopics = async (req, res) => {
    try {
        const topics = await Topics.find();
        if(topics.length === 0){
            res.status(404).json({message: "Topics not found"});
            return;
        }
        res.status(200).json({topics});
    } catch (error) {
        res.status(500).json({ error });
    }
}

const createTopic = async (req, res) => {
    const {token, topicNameEnglish, topicNameVietnamese, descriptionEnglish, descriptionVietNamese
    isPublic} = req.body;
}