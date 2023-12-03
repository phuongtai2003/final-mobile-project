package com.tdtu.finalproject.model.topic

import com.google.gson.annotations.SerializedName

data class Topic (
    @SerializedName("_id")
    val id: String,
    val topicNameEnglish: String,
    val topicNameVietnamese: String,
    val vocabularyCount: Int,
    val isPublic: Boolean,
    val upVoteCount: Int,
    val downVoteCount: Int,
    val descriptionEnglish: String,
    val descriptionVietnamese: String,
    @SerializedName("userId")
    val userId: List<String>,
    val learningStatisticsId: List<String>,
    val topicInFolderId: List<String>,
    val vocabularyId: List<String>,
)