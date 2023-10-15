const {VocabularyStatistic, Vocabulary} = require('../models');

const create_updateVocabularyStatistic = async (req, res) => {
    const userId = req.user.data._id;
    const vocabStats = req.body;

    try {
        for (let vocabStat of vocabStats) {
            const { vocabularyId, learningCount, learningStatus } = vocabStat;

            const vocab = await Vocabulary.findById(vocabularyId);
            if (!vocab) {
                return res.status(404).json({ error: 'Vocabulary does not exist' });
            }

            let stat = await VocabularyStatistic.findOne({ userId, vocabularyId });

            if (stat) {
                stat.learningCount += learningCount;
                stat.learningStatus = learningStatus;
                await stat.save();
            } else {
                stat = new VocabularyStatistic({
                    userId,
                    vocabularyId,
                    learningCount,
                    learningStatus
                });
                await stat.save();
            }
        }

        res.status(200).json({ message: 'Vocabulary statistics updated successfully' });
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
}


const getVocabularyStatisticById = async (req, res) => {

}

const getVocabularyStatisticByTopicId = async (req, res) => {
}

const getVocabularyStatisticByUserId = async (req, res) => {
}

module.exports = {
    create_updateVocabularyStatistic,
    getVocabularyStatisticById,
    getVocabularyStatisticByTopicId,
    getVocabularyStatisticByUserId
}