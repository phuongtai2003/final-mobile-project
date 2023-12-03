const express = require('express');
const rootRouter = express.Router();

const usersRouter = require('./users.router');
const topicsRouter = require('./topics.router');
const vocabulariesRouter = require('./vocabularies.router');
const foldersRouter = require('./folders.router');
// const bookmarkVocabulariesRouter = require('./bookmarkVocabularies.router');
const learningStatisticsRouter = require('./learningStatistics.router');
const vocabularyStatisticsRouter = require('./vocabularyStatistics.router');

rootRouter.use('/users', usersRouter);
rootRouter.use('/topics', topicsRouter);
rootRouter.use('/vocabularies', vocabulariesRouter);
rootRouter.use('/folders', foldersRouter);
// rootRouter.use('/bookmarkVocabularies', bookmarkVocabulariesRouter);
rootRouter.use('/learningStatistics', learningStatisticsRouter);
rootRouter.use('/vocabularyStatistics', vocabularyStatisticsRouter);

module.exports = rootRouter;