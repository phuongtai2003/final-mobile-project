package com.tdtu.finalproject.repository

import android.util.Log
import com.google.gson.Gson
import com.tdtu.finalproject.model.ErrorModel
import com.tdtu.finalproject.model.LoginRequest
import com.tdtu.finalproject.model.LoginResponse
import com.tdtu.finalproject.model.UpdateUserResponse
import com.tdtu.finalproject.model.RegisterRequest
import com.tdtu.finalproject.model.RegisterResponse
import com.tdtu.finalproject.model.UpdateUserInfoRequest
import com.tdtu.finalproject.model.UserInfo
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import java.io.File
import java.lang.Exception
import java.util.concurrent.CompletableFuture

class DataRepository() {

    companion object{
        private const val baseUrl: String = "http://10.0.2.2:3000/android/"
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
        val requestFile: RequestBody = RequestBody.create(MediaType.parse("image/*"), image)
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
}