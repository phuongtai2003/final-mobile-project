const express = require('express');
const bookmarkVocabulariesRouter = express.Router();

const { BookmarkVocabulary } = require('../models');
const { authentication } = require('../middlewares/authentication/authenticate');
const { isExistId, validateInput, checkId } = require('../middlewares/validation/validation');
const { createBookmarkVocabulary,
    deleteBookmarkVocabulary, getAllBookmarkVocabulary } = require('../controllers/bookmarkVocabularies.controller');

bookmarkVocabulariesRouter.post("/", authentication, createBookmarkVocabulary);

bookmarkVocabulariesRouter.delete("/:id", authentication, isExistId(BookmarkVocabulary), deleteBookmarkVocabulary);

bookmarkVocabulariesRouter.get("/", authentication, getAllBookmarkVocabulary);

module.exports = bookmarkVocabulariesRouter;