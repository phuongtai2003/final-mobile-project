const {Vocabulary, Topic} = require('../models');

const getVocabularies = async (req, res) => {
    try {
        const vocabularies = await Vocabulary.find();
        res.status(200).json({vocabularies, size: vocabularies.length});
    } catch (error) {
        res.status(500).json({ error : error.message });
    }
}

const getVocabularyById = async (req, res) => {
    const id = req.params.id || req.query.id;
    try {
        const vocabulary = await Vocabulary.findById(id);
        res.status(200).json({vocabulary});
    } catch (error) {
        res.status(500).json({ error : error.message });
    }
}

module.exports = {
    getVocabularies,
    getVocabularyById
}