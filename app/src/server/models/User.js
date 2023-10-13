const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const UserSchema = new Schema({
    email: { type: String, required: true, unique: true },
    username: { type: String, required: true },
    password: { type: String, required: true },
    profileImage: String,
    almaMater: { type: String, required: true },
    isPremiumAccount: { type: Boolean, default: false },
    bookmarkVocabularyId: [{ type: Schema.Types.ObjectId, ref: 'BookmarkVocabulary' }],
    vocabularyStatisticId: [{ type: Schema.Types.ObjectId, ref: 'VocabularyStatistic' }],
    folderId: [{ type: Schema.Types.ObjectId, ref: 'Folder' }],
    learningStatisticsId: [{ type: Schema.Types.ObjectId, ref: 'LearningStatistics' }],
    topicId : [{ type: Schema.Types.ObjectId, ref: 'Topic' }]
});

const User = mongoose.model('User', UserSchema);

module.exports = User;