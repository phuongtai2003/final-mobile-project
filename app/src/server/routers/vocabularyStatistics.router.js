const express = require('express');
const vocabularyStatisticsRouter = express.Router();

const { Topic, Users, Vocabulary } = require('../models');
const { authentication } = require('../middlewares/authentication/authenticate');

const {create_updateVocabularyStatistic,
    getVocabularyStatisticByTopicId,
    getVocabularyStatisticByUserId} = require('../controllers/vocabularyStatistics.controller');

// create or update vocabulary statistic (tested)
vocabularyStatisticsRouter.post("/", authentication , create_updateVocabularyStatistic);

module.exports = vocabularyStatisticsRouter;