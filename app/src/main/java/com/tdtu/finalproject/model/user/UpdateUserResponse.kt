package com.tdtu.finalproject.model.user

import com.google.gson.annotations.SerializedName
import com.tdtu.finalproject.model.user.User

data class UpdateUserResponse (
    @SerializedName("message")
    val message: String,
    val user: User
)