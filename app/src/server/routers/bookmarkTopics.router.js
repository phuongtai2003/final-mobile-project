const express = require('express');
const bookmarkVocabulariesRouter = express.Router();

const { BookmarkTopics } = require('../models');
const { authentication } = require('../middlewares/authentication/authenticate');
const { isExistId, validateInput, checkId } = require('../middlewares/validation/validation');
const { createBookmarkTopics,
    deleteBookmarkTopics } = require('../controllers/bookmarkVocabularies.controller');

bookmarkVocabulariesRouter.post("/", authentication, createBookmarkTopics);

bookmarkVocabulariesRouter.delete("/:id", authentication, isExistId(BookmarkTopics), deleteBookmarkTopics);

module.exports = bookmarkVocabulariesRouter;