package com.tdtu.finalproject.repository

import com.google.gson.Gson
import com.tdtu.finalproject.model.CreateTopicRequest
import com.tdtu.finalproject.model.CreateTopicResponse
import com.tdtu.finalproject.model.ErrorModel
import com.tdtu.finalproject.model.LoginRequest
import com.tdtu.finalproject.model.LoginResponse
import com.tdtu.finalproject.model.UpdateUserResponse
import com.tdtu.finalproject.model.RegisterRequest
import com.tdtu.finalproject.model.RegisterResponse
import com.tdtu.finalproject.model.UpdateUserInfoRequest
import com.tdtu.finalproject.model.UserInfo
import com.tdtu.finalproject.model.Vocabulary
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

class DataRepository() {

    companion object{
        private const val baseUrl: String = "http://192.168.1.39:3000/android/"
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
        var future: CompletableFuture<UserInfo> = CompletableFuture()
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
                    error = Gson().fromJson(response.errorBody()?.string(),ErrorModel::class.java).error
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
        var future: CompletableFuture<LoginResponse> = CompletableFuture()
        val loginRequest = LoginRequest(email, password)
        val call = api.login(loginRequest)
        var error: String? = null
        call.enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.code() == 200){
                    future.complete(response.body())
                }
                else if(response.code() == 401){
                    error = Gson().fromJson(response.errorBody()?.string(),ErrorModel::class.java).error
                    future.completeExceptionally(WrongCredentialsException(error!!))
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(),ErrorModel::class.java).error
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
        var future: CompletableFuture<UpdateUserResponse> = CompletableFuture()
        val updateRequest = UpdateUserInfoRequest(username, almaMater)
        val call = api.updateUser(updateRequest, id, token)
        var error: String? = null
        call.enqueue(object : Callback<UpdateUserResponse>{
            override fun onResponse(call: Call<UpdateUserResponse>, response: Response<UpdateUserResponse>) {
                if(response.code() == 200){
                    future.complete(response.body())
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(),ErrorModel::class.java).error
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
        var future: CompletableFuture<UpdateUserResponse> = CompletableFuture()
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
                    error = Gson().fromJson(response.errorBody()?.string(),ErrorModel::class.java).error
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
        var future: CompletableFuture<CreateTopicResponse> = CompletableFuture()
        val call = api.createNewTopic(CreateTopicRequest(topicNameEnglish, topicNameVietnamese, descriptionEnglish, descriptionVietnamese, vocabularyList, isPublic), token)
        var error: String? = null
        call.enqueue(object : Callback<CreateTopicResponse>{
            override fun onResponse(call: Call<CreateTopicResponse>, response: Response<CreateTopicResponse>) {
                if(response.code() == 200){
                    future.complete(response.body())
                }
                else{
                    error = Gson().fromJson(response.errorBody()?.string(),ErrorModel::class.java).error
                    future.completeExceptionally(Exception(error))
                }
            }
            override fun onFailure(call: Call<CreateTopicResponse>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }
}