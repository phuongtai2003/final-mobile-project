const express = require('express');
const vocabulariesRouter = express.Router();

const {getVocabularies, getVocabularyById} = require('../controllers/vocabularies.controller');

vocabulariesRouter.get("/", getVocabularies);
vocabulariesRouter.get("/:id", getVocabularyById);


module.exports = vocabulariesRouter;