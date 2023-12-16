package com.tdtu.finalproject.model.vocabulary_statistics

import com.google.gson.annotations.SerializedName

data class VocabularyStatistics (
    @SerializedName("_id")
    val id : String,
    val userId: String,
    val vocabularyId: String,
    val learningCount: Int,
    val learningStatus: String
)