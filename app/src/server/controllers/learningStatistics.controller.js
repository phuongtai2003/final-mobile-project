const {LearningStatistics} = require('../models');

const updateLearningStatistic = async (req, res) => {
    const { learningPercentage, learningTime, learningCount, vocabLearned } = req.body;
    const { topicId } = req.params;
    const userId = req.user._id;
    try {
        const learningStatistic = await LearningStatistics.findOne({ userId, topicId });
        if (!learningStatistic) {
            const newLearningStatistic = new LearningStatistics({ userId, topicId, learningPercentage, learningTime, learningCount, vocabLearned });
            await newLearningStatistic.save();
        }else{
            learningStatistic.learningPercentage = learningPercentage;
            learningStatistic.learningTime = learningTime;
            learningStatistic.learningCount = learningCount;
            learningStatistic.vocabLearned = vocabLearned;
            await learningStatistic.save();
        }
        return res.status(200).json({ message: 'Update learning statistic successfully', learningStatistic });
    } catch (error) {
        return res.status(500).json({ message: error.message });
    }
}

const getProcessLearning = async (req, res) => {
    const { topicId } = req.params;
    const userId = req.user._id;
    try {
        const learningStatistic = await LearningStatistics.findOne({ userId, topicId });
        if (!learningStatistic) {
            return res.status(200).json({ message: 'Get learning statistic successfully', learningStatistic: {userId, topicId ,learningPercentage: 0, learningTime: 0, learningCount: 0, vocabLearned: 0 } });
        }
        return res.status(200).json({ message: 'Get learning statistic successfully', learningStatistic });
    } catch (error) {
        return res.status(500).json({ message: error.message });
    }
}

module.exports = {
    updateLearningStatistic,
    getProcessLearning
}