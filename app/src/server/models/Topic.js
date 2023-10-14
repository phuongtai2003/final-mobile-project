const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const TopicSchema = new Schema({
    topicNameEnglish: { type: String, required: true },
    topicNameVietnamese: { type: String, required: true },
    vocabularyCount : { type : Number, default: 0 },
    isPublic : { type : Boolean, default: false },
    upVoteCount : { type : Number, default: 0 },
    downVoteCount : { type : Number, default: 0 },
    descriptionEnglish: { type: String, required: true },
    descriptionVietnamese: { type: String, required: true },
    userId: { type: Schema.Types.ObjectId, ref: 'User', required: true},
    learningStatisticsId: [{ type: Schema.Types.ObjectId, ref: 'LearningStatistics'}],
    topicInFolderId: [{ type: Schema.Types.ObjectId, ref: 'TopicInFolder' }],
    vocabularyId: [{ type: Schema.Types.ObjectId, ref: 'Vocabulary' }],
});

const Topic = mongoose.model('Topic', TopicSchema);
module.exports = Topic;