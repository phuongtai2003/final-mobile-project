const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const BookmarkTopicsSchema = new Schema({
    userId: { type: Schema.Types.ObjectId, ref: 'User', required: true },
    vocabularyId: { type: Schema.Types.ObjectId, ref: 'Vocabulary', required: true },
    bookmarkTime : { type : Date, default: Date.now }
});

const BookmarkTopics = mongoose.model('BookmarkTopics', BookmarkTopicsSchema);
module.exports = BookmarkTopics;