package com.tdtu.finalproject.repository

import com.tdtu.finalproject.model.topic.GetTopicsResponse
import com.tdtu.finalproject.model.topic.CreateTopicRequest
import com.tdtu.finalproject.model.topic.CreateTopicResponse
import com.tdtu.finalproject.model.user.LoginRequest
import com.tdtu.finalproject.model.user.LoginResponse
import com.tdtu.finalproject.model.user.UpdateUserResponse
import com.tdtu.finalproject.model.user.RegisterRequest
import com.tdtu.finalproject.model.user.RegisterResponse
import com.tdtu.finalproject.model.user.UpdateUserInfoRequest
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface API {
    @POST("users/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("users/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @PUT("users/profiles/{id}")
    fun updateUser(@Body request: UpdateUserInfoRequest, @Path("id") value: String, @Header("token") token: String): Call<UpdateUserResponse>

    @Multipart
    @PUT("users/profiles/change-profile-image/{id}")
    fun uploadImage(@Part image: MultipartBody.Part,@Path("id") value: String , @Header("token") token: String) : Call<UpdateUserResponse>

    @POST("topics/")
    fun createNewTopic(@Body request: CreateTopicRequest, @Header("token") token: String): Call<CreateTopicResponse>
    @GET("topics/users/{id}")
    fun getTopicsByUserId(@Path("id") userId: String, @Header("token") token: String): Call<GetTopicsResponse>
}