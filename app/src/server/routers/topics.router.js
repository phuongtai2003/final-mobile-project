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
    createVocabularyInTopic,
    deleteVocabularyInTopic,
    upVoteCount,
    downVoteCount,
    editVocabularyInTopic,
    importCSV,
    exportCSV} = require('../controllers/topics.controller');

// get all topics (tested)
topicsRouter.get("/", authentication, getAllTopics);
// get topic by id (tested)
topicsRouter.get("/:id", authentication, isExistId(Topic), getTopicById);
// create topic (tested)
topicsRouter.post("/", authentication, 
validateInput(['topicNameEnglish', 'topicNameVietnamese', 'descriptionEnglish', 'descriptionVietnamese', 'isPublic']), createTopic);
// update topic (tested)
topicsRouter.put("/:id", authentication, isExistId(Topic), updateTopic);
// delete topic (tested)
topicsRouter.delete("/:id", authentication, isExistId(Topic), deleteTopic)
// create vocab in topic (tested)
topicsRouter.post("/:id/vocabularies", authentication, isExistId(Topic), validateInput(["englishWord", "vietnameseWord", "englishMeaning", "vietnameseMeaning"]), createVocabularyInTopic);
// delete a vocab from topic
topicsRouter.delete("/:id/vocabularies/:vocabularyId", authentication, isExistId(Topic), deleteVocabularyInTopic);
// edit vocab from topic
topicsRouter.put("/:id/vocabularies/:vocabularyId", authentication, isExistId(Topic), editVocabularyInTopic);
// up vote topic (tested)
topicsRouter.put("/upvote/:id", authentication, isExistId(Topic), upVoteCount);
// down vote topic (tested)
topicsRouter.put("/downvote/:id", authentication, isExistId(Topic), downVoteCount);
// import csv
topicsRouter.post("/import-csv/:id",authentication, isExistId(Topic), importCSV);
// export csv
topicsRouter.get("/export-csv/:id",authentication, isExistId(Topic), exportCSV);

module.exports = topicsRouter;