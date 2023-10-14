package com.tdtu.finalproject.model

import com.google.gson.annotations.SerializedName

data class UpdateUserInfoRequest (
    @SerializedName("email")
    val email : String,
    @SerializedName("almaMater")
    val almaMater: String
)