package com.tdtu.finalproject.repository

import android.util.Log
import com.google.gson.Gson
import com.tdtu.finalproject.model.topic.GetTopicsResponse
import com.tdtu.finalproject.model.topic.CreateTopicRequest
import com.tdtu.finalproject.model.topic.CreateTopicResponse
import com.tdtu.finalproject.model.common.ErrorModel
import com.tdtu.finalproject.model.common.Message
import com.tdtu.finalproject.model.folder.AddTopicToFolderResponse
import com.tdtu.finalproject.model.folder.CreateFolderRequest
import com.tdtu.finalproject.model.folder.CreateFolderResponse
import com.tdtu.finalproject.model.folder.Folder
import com.tdtu.finalproject.model.folder.GetFolderByUserResponse
import com.tdtu.finalproject.model.folder.UpdateFolderRequest
import com.tdtu.finalproject.model.folder.UpdateFolderResponse
import com.tdtu.finalproject.model.topic.GetTopicByFolderResponse
import com.tdtu.finalproject.model.topic.GetTopicByIdResponse
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.model.user.LoginRequest
import com.tdtu.finalproject.model.user.LoginResponse
import com.tdtu.finalproject.model.user.UpdateUserResponse
import com.tdtu.finalproject.model.user.RegisterRequest
import com.tdtu.finalproject.model.user.RegisterResponse
import com.tdtu.finalproject.model.user.UpdateUserInfoRequest
import com.tdtu.finalproject.model.user.User
import com.tdtu.finalproject.model.user.UserInfo
import com.tdtu.finalproject.model.vocabulary.BookmarkVocabulariesRequest
import com.tdtu.finalproject.model.vocabulary.GetBookmarkVocabulariesResponse
import com.tdtu.finalproject.model.vocabulary.Vocabulary
import com.tdtu.finalproject.model.vocabulary.GetVocabulariesByTopicResponse
import com.tdtu.finalproject.utils.WrongCredentialsException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.lang.Exception
import java.util.concurrent.CompletableFuture

class DataRepository {

    companion object{
        private const val baseUrl: String = "http://192.168.1.8:3000/android/"
        private val api = Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build().create(API::class.java)
        private var instance: DataRepository? = null
        fun getInstance() : DataRepository{
            if(this.instance == null){
                this.instance = DataRepository()
            }
            return instance as DataRepository
        }
    }

    fun register(username : String, email: String, almaMater:String, password : String) : CompletableFuture<UserInfo> {
        val future: CompletableFuture<UserInfo> = CompletableFuture()
        val userData = RegisterRequest(email, username, password, almaMater)
        val call = api.register(userData)
        var error : String
        call.enqueue(object: Callback<RegisterResponse>{
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if(response.code() == 200){
                    future.complete(response.body()?.user)
                } else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }

    fun login(email: String, password: String) : CompletableFuture<LoginResponse>{
        val future: CompletableFuture<LoginResponse> = CompletableFuture()
        val loginRequest = LoginRequest(email, password)
        val call = api.login(loginRequest)
        var error: String? = null
        call.enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.code() == 200){
                    future.complete(response.body())
                }
                else if(response.code() == 401){
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(WrongCredentialsException(error!!))
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }

    fun updateUser(username: String, almaMater: String, id: String, token: String) : CompletableFuture<UpdateUserResponse>{
        val future: CompletableFuture<UpdateUserResponse> = CompletableFuture()
        val updateRequest = UpdateUserInfoRequest(username, almaMater)
        val call = api.updateUser(updateRequest, id, token)
        var error: String? = null
        call.enqueue(object : Callback<UpdateUserResponse>{
            override fun onResponse(call: Call<UpdateUserResponse>, response: Response<UpdateUserResponse>) {
                if(response.code() == 200){
                    future.complete(response.body())
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }
            }
            override fun onFailure(call: Call<UpdateUserResponse>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }

    fun uploadImage(image: File, id: String, token: String) : CompletableFuture<UpdateUserResponse>{
        val future: CompletableFuture<UpdateUserResponse> = CompletableFuture()
        val mediaType = "image/*".toMediaType()
        val requestFile: RequestBody = image.asRequestBody(mediaType)
        val file : MultipartBody.Part = MultipartBody.Part.createFormData("image", image.name ,requestFile)
        val call = api.uploadImage(file, id, token)
        var error: String? = null
        call.enqueue(object : Callback<UpdateUserResponse>{
            override fun onResponse(call: Call<UpdateUserResponse>, response: Response<UpdateUserResponse>) {
                if(response.code() == 200){
                    future.complete(response.body())
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }
            }
            override fun onFailure(call: Call<UpdateUserResponse>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }

    fun createTopic(topicNameEnglish: String, topicNameVietnamese: String, descriptionEnglish: String, descriptionVietnamese: String, vocabularyList: List<Vocabulary>, isPublic: Boolean, token: String) : CompletableFuture<CreateTopicResponse>{
        val future: CompletableFuture<CreateTopicResponse> = CompletableFuture()
        val call = api.createNewTopic(CreateTopicRequest(topicNameEnglish, topicNameVietnamese, descriptionEnglish, descriptionVietnamese, vocabularyList, isPublic), token)
        var error: String? = null
        call.enqueue(object : Callback<CreateTopicResponse>{
            override fun onResponse(call: Call<CreateTopicResponse>, response: Response<CreateTopicResponse>) {
                if(response.code() == 200){
                    future.complete(response.body())
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }
            }
            override fun onFailure(call: Call<CreateTopicResponse>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }

    fun getTopicsByUserId(userId: String, token: String) : CompletableFuture<List<Topic>>{
        val future: CompletableFuture<List<Topic>> = CompletableFuture()
        val call = api.getTopicsByUserId(userId, token)
        var error: String?
        call.enqueue(object : Callback<GetTopicsResponse>{
            override fun onResponse(call: Call<GetTopicsResponse>, response: Response<GetTopicsResponse>) {
                if(response.code() == 200){
                    future.complete(response.body()?.topics)
                }
                else if(response.code() == 404){
                    future.complete(ArrayList<Topic>())
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }
            }
            override fun onFailure(call: Call<GetTopicsResponse>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }

    fun createFolder(folderNameEnglish: String, folderNameVietnamese: String, token: String) : CompletableFuture<CreateFolderResponse>{
        val future: CompletableFuture<CreateFolderResponse> = CompletableFuture()
        val call = api.createNewFolder(CreateFolderRequest(folderNameEnglish, folderNameVietnamese), token)
        var error: String? = null
        call.enqueue(object : Callback<CreateFolderResponse>{
            override fun onResponse(call: Call<CreateFolderResponse>, response: Response<CreateFolderResponse>) {
                if(response.code() == 200){
                    future.complete(response.body())
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }
            }
            override fun onFailure(call: Call<CreateFolderResponse>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }

    fun getFolderByUser(userId: String, token: String) : CompletableFuture<List<Folder>>{
        val future: CompletableFuture<List<Folder>> = CompletableFuture()
        val call = api.getFoldersByUserId(userId, token)
        var error: String? = null
        call.enqueue(object : Callback<GetFolderByUserResponse>{
            override fun onResponse(call: Call<GetFolderByUserResponse>, response: Response<GetFolderByUserResponse>) {
                if(response.code() == 200){
                    future.complete(response.body()?.folders)
                }
                else if(response.code() == 404){
                    future.complete(ArrayList<Folder>())
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }
            }
            override fun onFailure(call: Call<GetFolderByUserResponse>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }

    fun addTopicToFolder(folderId: String, topicId: String, token:String) : CompletableFuture<Boolean>{
        val future: CompletableFuture<Boolean> = CompletableFuture()
        val call = api.addTopicToFolder(folderId, topicId, token)
        var error: String?
        call.enqueue(object : Callback<AddTopicToFolderResponse>{
            override fun onResponse(call: Call<AddTopicToFolderResponse>, response: Response<AddTopicToFolderResponse>) {
                if(response.code() == 200){
                    future.complete(true)
                }
                else if(response.code() == 409){
                    future.complete(true)
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }
            }
            override fun onFailure(call: Call<AddTopicToFolderResponse>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }

    fun getTopicById(topicId: String, token: String): CompletableFuture<Topic>{
        val future: CompletableFuture<Topic> = CompletableFuture()
        val call = api.getTopicById(topicId, token)
        var error: String?
        call.enqueue(object : Callback<GetTopicByIdResponse>{
            override fun onResponse(call: Call<GetTopicByIdResponse>, response: Response<GetTopicByIdResponse>) {
                if(response.code() == 200){
                    future.complete(response.body()?.topic)
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }
            }
            override fun onFailure(call: Call<GetTopicByIdResponse>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }

    fun getTopicByFolderId(folderId: String, token: String) : CompletableFuture<List<Topic>>{
        val future: CompletableFuture<List<Topic>> = CompletableFuture()
        val call = api.getTopicsByFolderId(folderId, token)
        var error: String?
        call.enqueue(object : Callback<GetTopicByFolderResponse>{
            override fun onResponse(call: Call<GetTopicByFolderResponse>, response: Response<GetTopicByFolderResponse>) {
                if(response.code() == 200){
                    if(response.body()?.topics != null && response.body()?.topics!!.isNotEmpty()){
                        val result = ArrayList<Topic>()
                        for (topic in response.body()?.topics!!){
                            result.add(topic.topic)
                        }
                        future.complete(result)
                    }
                    else{
                        future.complete(ArrayList())
                    }
                }
                else if(response.code() == 404){
                    future.complete(ArrayList())
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }
            }
            override fun onFailure(call: Call<GetTopicByFolderResponse>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }

    fun updateFolder(folderId: String,folderNameEnglish: String, folderNameVietnamese: String, token: String): CompletableFuture<Folder>{
        val future: CompletableFuture<Folder> = CompletableFuture()
        val call = api.updateFolder(folderId, token, UpdateFolderRequest(folderNameEnglish, folderNameVietnamese))
        var error: String?
        call.enqueue(object : Callback<UpdateFolderResponse>{
            override fun onResponse(call: Call<UpdateFolderResponse>, response: Response<UpdateFolderResponse>) {
                if(response.code() == 200){
                    future.complete(response.body()?.folder)
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }
            }
            override fun onFailure(call: Call<UpdateFolderResponse>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }

    fun deleteFolder(folderId: String, token: String): CompletableFuture<Boolean>{
        val future: CompletableFuture<Boolean> = CompletableFuture()
        val call = api.deleteFolder(folderId, token)
        var error: String?
        call.enqueue(object : Callback<Message>{
            override fun onResponse(call: Call<Message>, response: Response<Message>) {
                if(response.code() == 200){
                    future.complete(true)
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }
            }
            override fun onFailure(call: Call<Message>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }

    fun deleteTopicFromFolder(folderId: String, topicId: String, token: String): CompletableFuture<Boolean>{
        val future: CompletableFuture<Boolean> = CompletableFuture()
        val call = api.deleteTopicFromFolder(folderId, topicId, token)
        var error: String?
        call.enqueue(object : Callback<CreateFolderResponse>{
            override fun onResponse(call: Call<CreateFolderResponse>, response: Response<CreateFolderResponse>) {
                if(response.code() == 200){
                    future.complete(true)
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }
            }
            override fun onFailure(call: Call<CreateFolderResponse>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }

    fun getVocabulariesByTopic(topicId: String, token: String): CompletableFuture<List<Vocabulary>> {
        val future: CompletableFuture<List<Vocabulary>> = CompletableFuture()
        val call = api.getVocabulariesByTopicId(topicId, token)
        var error: String?
        call.enqueue(object : Callback<GetVocabulariesByTopicResponse>{
            override fun onResponse(call: Call<GetVocabulariesByTopicResponse>, response: Response<GetVocabulariesByTopicResponse>) {
                if(response.code() == 200){
                    future.complete(response.body()?.vocabularies)
                }
                else if(response.code() == 404){
                    future.complete(ArrayList())
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }

            }
            override fun onFailure(call: Call<GetVocabulariesByTopicResponse>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }

    fun getUserById(userId: String, token: String): CompletableFuture<User>{
        val future: CompletableFuture<User> = CompletableFuture()
        val call = api.getUserById(userId, token)
        var error: String?
        call.enqueue(object : Callback<UpdateUserResponse>{
            override fun onResponse(call: Call<UpdateUserResponse>, response: Response<UpdateUserResponse>) {
                if(response.code() == 200){
                    future.complete(response.body()?.user)
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }

            }
            override fun onFailure(call: Call<UpdateUserResponse>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }

    fun deleteTopic(topicId: String, token: String): CompletableFuture<Boolean>{
        val future: CompletableFuture<Boolean> = CompletableFuture()
        val call = api.deleteTopic(topicId, token)
        var error: String?
        call.enqueue(object : Callback<Message>{
            override fun onResponse(call: Call<Message>, response: Response<Message>) {
                if(response.code() == 200){
                    future.complete(true)
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }
            }
            override fun onFailure(call: Call<Message>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }

    fun addVocabularyToTopic(topicId: String, token: String, vocabulary: Vocabulary): CompletableFuture<Boolean>{
        val future: CompletableFuture<Boolean> = CompletableFuture()
        val call = api.addVocabularyToTopic(topicId, token, vocabulary)
        var error: String?
        call.enqueue(object : Callback<Message>{
            override fun onResponse(call: Call<Message>, response: Response<Message>) {
                if(response.code() == 200){
                    future.complete(true)
                }
                else if(response.code() == 409){
                    future.complete(true)
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }

            }
            override fun onFailure(call: Call<Message>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }

    fun editVocabularyInTopic(topicId: String, token: String, vocabulary: Vocabulary): CompletableFuture<Boolean>{
        val future: CompletableFuture<Boolean> = CompletableFuture()
        val call = api.updateVocabularyFromTopic(topicId, vocabulary.id!!,token, vocabulary)
        var error: String?
        call.enqueue(object : Callback<Message>{
            override fun onResponse(call: Call<Message>, response: Response<Message>) {
                if(response.code() == 200){
                    future.complete(true)
                }
                else if(response.code() == 409){
                    future.complete(true)
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }

            }
            override fun onFailure(call: Call<Message>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }

    fun deleteVocabularyFromTopic(topicId: String, token: String, vocabularyId: String): CompletableFuture<Boolean>{
        val future: CompletableFuture<Boolean> = CompletableFuture()
        val call = api.deleteVocabularyFromTopic(topicId, vocabularyId, token)
        var error: String?
        call.enqueue(object : Callback<Message>{
            override fun onResponse(call: Call<Message>, response: Response<Message>) {
                if(response.code() == 200){
                    future.complete(true)
                }
                else if(response.code() == 404){
                    future.complete(true)
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }

            }
            override fun onFailure(call: Call<Message>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }

    fun updateTopic(topicId: String, token: String, topic: Topic): CompletableFuture<Topic>{
        val future: CompletableFuture<Topic> = CompletableFuture()
        val call = api.updateTopic(topicId, token, topic)
        var error: String?
        call.enqueue(object : Callback<GetTopicByIdResponse>{
            override fun onResponse(call: Call<GetTopicByIdResponse>, response: Response<GetTopicByIdResponse>) {
                if(response.code() == 200){
                    future.complete(response.body()?.topic)
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }

            }
            override fun onFailure(call: Call<GetTopicByIdResponse>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }
    fun getFoldersByTopic(topicId: String, token: String): CompletableFuture<List<Folder>>{
        val future: CompletableFuture<List<Folder>> = CompletableFuture()
        val call = api.getFoldersByTopicId(topicId, token)
        Log.d("USER TAG", "getFoldersByTopic: $topicId")
        var error: String?
        call.enqueue(object : Callback<GetFolderByUserResponse>{
            override fun onResponse(call: Call<GetFolderByUserResponse>, response: Response<GetFolderByUserResponse>) {
                if(response.code() == 200){
                    future.complete(response.body()?.folders)
                }
                else if(response.code() == 404){
                    future.complete(ArrayList())
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }

            }
            override fun onFailure(call: Call<GetFolderByUserResponse>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }
    fun createBookmarkVocabularies(vocabularies: List<Vocabulary>, token: String): CompletableFuture<Boolean>{
        val future: CompletableFuture<Boolean> = CompletableFuture()
        val call = api.createBookmarkVocabulary(token, BookmarkVocabulariesRequest(vocabularies))
        var error: String?
        call.enqueue(object : Callback<Message>{
            override fun onResponse(call: Call<Message>, response: Response<Message>) {
                if(response.code() == 200){
                    future.complete(true)
                }
                else if(response.code() == 400){
                    future.complete(true)
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }

            }
            override fun onFailure(call: Call<Message>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }
    fun removeBookmarkedVocabularies(token: String, vocabularyId: String): CompletableFuture<Boolean>{
        val future: CompletableFuture<Boolean> = CompletableFuture()
        val call = api.removeBookmarkVocabulary(token, vocabularyId)
        var error: String?
        call.enqueue(object : Callback<Message>{
            override fun onResponse(call: Call<Message>, response: Response<Message>) {
                if(response.code() == 200){
                    future.complete(true)
                }
                else if(response.code() == 404){
                    future.complete(true)
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }
            }
            override fun onFailure(call: Call<Message>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }

    fun getBookmarkVocabulariesInTopic(token: String, topicId: String): CompletableFuture<List<Vocabulary>>{
        val future: CompletableFuture<List<Vocabulary>> = CompletableFuture()
        val call = api.getBookmarkVocabulariesInTopic(token, topicId)
        var error: String?
        call.enqueue(object : Callback<GetBookmarkVocabulariesResponse>{
            override fun onResponse(call: Call<GetBookmarkVocabulariesResponse>, response: Response<GetBookmarkVocabulariesResponse>) {
                val resultList = ArrayList<Vocabulary>()
                if(response.code() == 200){
                    for (vocabulary in response.body()?.bookmarkVocabs!!){
                        resultList.add(vocabulary)
                    }
                    future.complete(resultList)
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(), ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }

            }
            override fun onFailure(call: Call<GetBookmarkVocabulariesResponse>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }
}