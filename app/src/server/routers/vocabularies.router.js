const express = require('express');
const vocabulariesRouter = express.Router();

const {createVocabulary} = require('../controllers/vocabularies.controller');

vocabulariesRouter.get("/");

vocabulariesRouter.post("/", createVocabulary);

module.exports = vocabulariesRouter;