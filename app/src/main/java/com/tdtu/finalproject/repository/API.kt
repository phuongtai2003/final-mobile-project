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
import com.tdtu.finalproject.model.topic.UpdateLearningStatisticsRequest
import com.tdtu.finalproject.model.user.ChangePasswordRequest
import com.tdtu.finalproject.model.user.LoginRequest
import com.tdtu.finalproject.model.user.LoginResponse
import com.tdtu.finalproject.model.user.RecoverPasswordRequest
import com.tdtu.finalproject.model.user.UpdateUserResponse
import com.tdtu.finalproject.model.user.RegisterRequest
import com.tdtu.finalproject.model.user.RegisterResponse
import com.tdtu.finalproject.model.user.UpdateUserInfoRequest
import com.tdtu.finalproject.model.vocabulary.BookmarkVocabulariesRequest
import com.tdtu.finalproject.model.vocabulary.GetBookmarkVocabulariesResponse
import com.tdtu.finalproject.model.vocabulary.GetVocabulariesByTopicResponse
import com.tdtu.finalproject.model.vocabulary.StudyVocabularyRequest
import com.tdtu.finalproject.model.vocabulary.Vocabulary
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
    @GET("vocabularies/topics/{topicId}")
    fun getVocabulariesByTopicId(@Path("topicId") topicId: String, @Header("token") token: String): Call<GetVocabulariesByTopicResponse>
    @GET("users/{id}")
    fun getUserById(@Path("id") userId: String, @Header("token") token: String): Call<UpdateUserResponse>
    @DELETE("topics/{id}")
    fun deleteTopic(@Path("id") topicId: String, @Header("token") token: String) : Call<Message>
    @POST("topics/{id}/vocabularies")
    fun addVocabularyToTopic(@Path("id") topicId: String, @Header("token") token: String, @Body() vocabulary: Vocabulary) : Call<Message>
    @DELETE("topics/{id}/vocabularies/{vocabularyId}")
    fun deleteVocabularyFromTopic(@Path("id") topicId: String, @Path("vocabularyId") vocabularyId: String, @Header("token") token: String) : Call<Message>
    @PUT("topics/{id}/vocabularies/{vocabularyId}")
    fun updateVocabularyFromTopic(@Path("id") topicId: String, @Path("vocabularyId") vocabularyId: String, @Header("token") token: String, @Body() vocabulary: Vocabulary) : Call<Message>
    @PUT("topics/{id}")
    fun updateTopic(@Path("id") topicId: String, @Header("token") token: String, @Body() topic: Topic) : Call<GetTopicByIdResponse>
    @GET("topics/{id}/folder")
    fun getFoldersByTopicId(@Path("id") topicId: String, @Header("token") token: String) : Call<GetFolderByUserResponse>
    @POST("bookmarkVocabularies/")
    fun createBookmarkVocabulary(@Header("token") token: String, @Body() request: BookmarkVocabulariesRequest) : Call<Message>
    @GET("bookmarkVocabularies/")
    fun getBookmarkVocabularies(@Header("token") token: String) : Call<GetBookmarkVocabulariesResponse>
    @DELETE("bookmarkVocabularies/vocabularies/{vocabularyId}")
    fun removeBookmarkVocabulary(@Header("token") token: String, @Path("vocabularyId") vocabularyId: String) : Call<Message>
    @GET("topics/{id}/bookmark")
    fun getBookmarkVocabulariesInTopic(@Header("token") token: String, @Path("id") topicId: String) : Call<GetBookmarkVocabulariesResponse>
    @GET("topics/public/getPublicTopic")
    fun getPublicTopics(@Header("token") token: String) : Call<GetTopicsResponse>
    @POST("vocabularyStatistics/")
    fun studyVocabulary(@Header("token") token: String, @Body() request: StudyVocabularyRequest) : Call<Message>
    @POST("topics/public/learnTopic/{id}")
    fun learnTopic(@Header("token") token: String, @Path("id") topicId: String) : Call<Message>
    @PUT("learningStatistics/topic/{topicId}/progress")
    fun updateTopicProgress(@Header("token") token: String, @Path("topicId") topicId: String, @Body() updateLearningStatisticsRequest: UpdateLearningStatisticsRequest) : Call<Message>
    @POST("users/recover-password")
    fun recoverPassword(@Body() recoverPasswordRequest: RecoverPasswordRequest) : Call<Message>
    @PUT("users/profiles/password/{id}")
    fun changePassword(@Header("token") token: String,@Path("id") userId: String, @Body() changePasswordRequest: ChangePasswordRequest) : Call<Message>
}