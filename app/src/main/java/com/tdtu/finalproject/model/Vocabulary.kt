package com.tdtu.finalproject.model

data class Vocabulary (
    var id: String?,
    var englishWord: String,
    var vietnameseWord: String,
    var englishMeaning: String,
    var vietnameseMeaning: String,
    var topicId: String?,
    var vocabularyStatisticId: String?,
    var bookmarkVocabularyId: String?
)