package com.tdtu.finalproject.model

import com.fasterxml.jackson.annotation.JsonProperty

data class LoginResponse(
    val user: User,
    val token: String,
    val error: String
)

data class User(
    @JsonProperty("_id")
    val id: String,
    val email: String,
    val username: String,
    val profileImage: String,
    val almaMater: String,
    val isPremiumAccount: Boolean,
    val bookmarkVocabularyId: List<Any?>,
    val vocabularyStatisticId: List<Any?>,
    val folderId: List<Any?>,
    val learningStatisticsId: List<Any?>,
    val topicId: List<Any?>,
    @JsonProperty("__v")
    val v: Long,
)
