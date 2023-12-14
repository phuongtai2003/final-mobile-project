package com.tdtu.finalproject.model.learning_statistics

import com.google.gson.annotations.SerializedName
import com.tdtu.finalproject.model.user.User

data class LearningStatistic (
    @SerializedName("_id")
    val id:String,
    val userId: User,
    val topicId: String,
    val learningPercentage: Double,
    val learningTime: Int,
    val learningCount: Int,
    val vocabLearned: Int,
    @SerializedName("__v")
    val v:Int
)