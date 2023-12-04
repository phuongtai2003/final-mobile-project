package com.tdtu.finalproject.repository

import com.tdtu.finalproject.model.common.Message
import com.tdtu.finalproject.model.folder.AddTopicToFolderResponse
import com.tdtu.finalproject.model.folder.CreateFolderRequest
import com.tdtu.finalproject.model.folder.CreateFolderResponse
import com.tdtu.finalproject.model.folder.Folder
import com.tdtu.finalproject.model.folder.GetFolderByUserResponse
import com.tdtu.finalproject.model.folder.UpdateFolderRequest
import com.tdtu.finalproject.model.folder.UpdateFolderResponse
import com.tdtu.finalproject.model.topic.GetTopicsResponse
import com.tdtu.finalproject.model.topic.CreateTopicRequest
import com.tdtu.finalproject.model.topic.CreateTopicResponse
import com.tdtu.finalproject.model.topic.GetTopicByFolderResponse
import com.tdtu.finalproject.model.topic.GetTopicByIdResponse
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.model.user.LoginRequest
import com.tdtu.finalproject.model.user.LoginResponse
import com.tdtu.finalproject.model.user.UpdateUserResponse
import com.tdtu.finalproject.model.user.RegisterRequest
import com.tdtu.finalproject.model.user.RegisterResponse
import com.tdtu.finalproject.model.user.UpdateUserInfoRequest
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
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
    @POST("folders/")
    fun createNewFolder(@Body request: CreateFolderRequest, @Header("token") token: String): Call<CreateFolderResponse>
    @GET("folders/users/{id}")
    fun getFoldersByUserId(@Path("id") userId: String, @Header("token") token: String): Call<GetFolderByUserResponse>
    @POST("folders/{id}/topics/{topicId}")
    fun addTopicToFolder(@Path("id") folderId: String, @Path("topicId") topicId: String, @Header("token") token: String): Call<AddTopicToFolderResponse>
    @GET("topics/folders/{folderId}")
    fun getTopicsByFolderId(@Path("folderId") folderId: String, @Header("token") token: String): Call<GetTopicByFolderResponse>
    @GET("topics/{id}")
    fun getTopicById(@Path("id") topicId: String, @Header("token") token: String): Call<GetTopicByIdResponse>
    @PUT("folders/{id}")
    fun updateFolder(@Path("id") folderId: String, @Header("token") token: String, @Body() updateFolderRequest: UpdateFolderRequest) : Call<UpdateFolderResponse>
    @DELETE("folders/{id}")
    fun deleteFolder(@Path("id") folderId: String, @Header("token") token: String) : Call<Message>
    @DELETE("folders/{id}/topics/{topicId}")
    fun deleteTopicFromFolder(@Path("id") folderId: String, @Path("topicId") topicId: String, @Header("token") token: String) : Call<CreateFolderResponse>
}