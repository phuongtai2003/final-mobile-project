const express = require('express');
const vocabulariesRouter = express.Router();

const {getVocabularies, getVocabularyById, getVocabularyByTopicId} = require('../controllers/vocabularies.controller');

// get all
vocabulariesRouter.get("/", getVocabularies);
// get by id
vocabulariesRouter.get("/:id", getVocabularyById);
// get by topic id
vocabulariesRouter.get("/topics/:topicId", getVocabularyByTopicId);


module.exports = vocabulariesRouter;