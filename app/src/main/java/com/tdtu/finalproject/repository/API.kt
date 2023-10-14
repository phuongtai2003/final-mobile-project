package com.tdtu.finalproject.repository

import com.tdtu.finalproject.model.LoginRequest
import com.tdtu.finalproject.model.LoginResponse
import com.tdtu.finalproject.model.UpdateUserResponse
import com.tdtu.finalproject.model.RegisterRequest
import com.tdtu.finalproject.model.RegisterResponse
import com.tdtu.finalproject.model.UpdateUserInfoRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface API {
    @POST("users/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("users/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @PUT("users/profiles/{id}")
    fun updateUser(@Body request: UpdateUserInfoRequest, @Path("id") value: String, @Header("token") token: String): Call<UpdateUserResponse>
}