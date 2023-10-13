const {Vocabulary} = require('../models');

const createVocabulary = async (req, res) => {
    const {englishWord, vietnameseWord, englishMeaning, vietnameseMeaning} = req.body;
    try {
        console.log(req.body.user);
        const vocabulary = await Vocabulary.create({englishWord, vietnameseWord, englishMeaning, vietnameseMeaning});
        res.status(200).json({vocabulary});
    }catch (error) {
        res.status(500).json({error});
    }
}

module.exports = {
    createVocabulary
}