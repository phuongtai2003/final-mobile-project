package com.tdtu.finalproject.model

import com.google.gson.annotations.SerializedName

data class UpdateUserResponse (
    @SerializedName("message")
    val message: String,
    val user: User
)