package com.tdtu.finalproject.model

data class RegisterResponse(
    val error: String,
    val user: UserInfo
)

data class UserInfo(
    val email: String,
    val username: String,
    val profileImage: String,
    val almaMater: String,
    val isPremiumAccount: Boolean,
    val bookmarkVocabularyId: List<String>,
    val vocabularyStatisticId: List<String>,
    val folderId: List<String>,
    val learningStatisticsId: List<String>,
    val topicId: List<String>,
    val _id: String,
    val __v: Int
)
