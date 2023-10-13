package com.tdtu.finalproject.repository

import com.tdtu.finalproject.model.LoginRequest
import com.tdtu.finalproject.model.LoginResponse
import com.tdtu.finalproject.model.PageViewItem
import com.tdtu.finalproject.model.RegisterRequest
import com.tdtu.finalproject.model.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface API {
    @POST("users/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("users/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}