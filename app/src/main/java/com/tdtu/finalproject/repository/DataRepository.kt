package com.tdtu.finalproject.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.tdtu.finalproject.model.LoginRequest
import com.tdtu.finalproject.model.LoginResponse
import com.tdtu.finalproject.model.RegisterRequest
import com.tdtu.finalproject.model.RegisterResponse
import com.tdtu.finalproject.model.User
import com.tdtu.finalproject.model.UserInfo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
                    error = response.body()?.error.toString()
                    future.completeExceptionally(Exception(error))
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }

    fun login(username: String, password: String) : CompletableFuture<User>{
        var future: CompletableFuture<User> = CompletableFuture()
        val loginRequest = LoginRequest(username, password)
        val call = api.login(loginRequest)
        var error: String? = null
        call.enqueue(object : Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.code() == 200){
                    Log.d("Res status", "onResponse: " + response.body()?.user?.username)
                    future.complete(response.body()?.user)
                }
                else{
                    val errorBody = response.errorBody()?.string()
                    Log.d("Res status", "onResponse: $errorBody")
                    future.completeExceptionally(Exception(errorBody))
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                future.completeExceptionally(t)
            }
        })
        return future
    }
}