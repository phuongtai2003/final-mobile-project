const express = require('express');
const topicsRouter = express.Router();

const { Topic } = require('../models');
const { authentication } = require('../middlewares/authentication/authenticate');
const {isCreated, isExistId, validateInput, isExistEmail, isCreatedUsername} = require('../middlewares/validation/validation');
const {getTopicById,
    getAllTopics,
    createTopic,
    updateTopic,
    deleteTopic,
    addVocabularyToTopic,
    deleteVocabularyFromTopic,
    upVoteCount,
    downVoteCount} = require('../controllers/topics.controller');

// get all topics (tested)
topicsRouter.get("/", authentication, getAllTopics);
// get topic by id (tested)
topicsRouter.get("/:id", authentication, isExistId(Topic), getTopicById);
// create topic (tested)
topicsRouter.post("/", authentication, 
validateInput(['topicNameEnglish', 'topicNameVietnamese', 'descriptionEnglish', 'descriptionVietnamese', 'isPublic']),
createTopic);
// update topic (tested)
topicsRouter.put("/:id", authentication, isExistId(Topic), updateTopic);
// delete topic (tested)
topicsRouter.delete("/:id", authentication, isExistId(Topic), deleteTopic)
// add vocab to topic
topicsRouter.post("/:id/vocabularies", authentication, isExistId(Topic), validateInput(["englishWord", "vietnameseWord", "englishMeaning", "vietnameseMeaning"]), addVocabularyToTopic);
// delete a vocab from topic
topicsRouter.delete("/:id/vocabularies/:vocabularyId", authentication, isExistId(Topic), deleteVocabularyFromTopic);
// up vote topic
topicsRouter.put("/upvote/:id", authentication, isExistId(Topic), upVoteCount);
// down vote topic
topicsRouter.put("/downvote/:id", authentication, isExistId(Topic), downVoteCount);

module.exports = topicsRouter;