package com.tdtu.finalproject.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val user: User,
    val token: String,
    val error: String
)

data class User(
    @SerializedName("_id")
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
    @SerializedName("__v")
    val v: Long,
)
