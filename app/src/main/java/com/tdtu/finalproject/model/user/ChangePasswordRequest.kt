package com.tdtu.finalproject.model.user
data class ChangePasswordRequest(
    val newPassword: String,
    val password: String
)