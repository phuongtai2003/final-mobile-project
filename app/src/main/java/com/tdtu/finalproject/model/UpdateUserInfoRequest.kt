package com.tdtu.finalproject.model

import com.google.gson.annotations.SerializedName

data class UpdateUserInfoRequest (
    @SerializedName("username")
    val username : String,
    @SerializedName("almaMater")
    val almaMater: String
)