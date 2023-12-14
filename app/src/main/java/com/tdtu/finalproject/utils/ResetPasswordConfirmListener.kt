package com.tdtu.finalproject.utils

interface ResetPasswordConfirmListener {
    fun onConfirm(email: String)
    fun changePassword(oldPassword: String, newPassword: String)
}