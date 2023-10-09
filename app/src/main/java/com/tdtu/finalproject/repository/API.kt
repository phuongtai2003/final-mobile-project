package com.tdtu.finalproject.repository

import com.tdtu.finalproject.model.PageViewItem
import retrofit2.Call
import retrofit2.http.GET

interface API {
    @GET("comments")
    fun getComments(): Call<ArrayList<PageViewItem>>
}