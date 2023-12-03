package com.tdtu.finalproject.model.topic

data class CreateTopicRequest (
    val topicNameEnglish: String,
    val topicNameVietnamese: String,
    val descriptionEnglish: String,
    val descriptionVietnamese: String,
    val vocabularyList: List<Vocabulary>,
    val isPublic: Boolean
)