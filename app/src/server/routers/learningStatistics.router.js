const express = require('express');
const learningStatisticsRouter = express.Router();

const {LearningStatistics} = require('../models');
const { authentication } = require('../middlewares/authentication/authenticate');
const { isExistId, validateInput, checkId } = require('../middlewares/validation/validation');
const { getProcessLearning, updateLearningStatistic } = require('../controllers/learningStatistics.controller');
// get process learning
learningStatisticsRouter.get("/topic/:topicId/progress", authentication, getProcessLearning);
// update learning statistic
learningStatisticsRouter.put("/topic/:topicId/progress", authentication, updateLearningStatistic);

module.exports = learningStatisticsRouter;