package com.tdtu.finalproject.model.achievement

import com.google.gson.annotations.SerializedName

data class Achievement (
    @SerializedName("_id")
    val id: String,
    val name: String,
    val description: String,
    val image: String,
)