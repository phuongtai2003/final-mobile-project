package com.tdtu.finalproject.repository

import com.tdtu.finalproject.model.PageViewItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DataRepository(private val baseUrl: String = "http://localhost/", private val api: API = Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build().create(API::class.java)) {
    fun getComments() : ArrayList<PageViewItem>{
        var arraylist: ArrayList<PageViewItem> = ArrayList<PageViewItem>()
        api.getComments().enqueue(object : Callback<ArrayList<PageViewItem>>{
            override fun onResponse(
                call: Call<ArrayList<PageViewItem>>,
                response: Response<ArrayList<PageViewItem>>
            ) {
                response.body()?.let { arraylist.addAll(it) }
            }
            override fun onFailure(call: Call<ArrayList<PageViewItem>>, t: Throwable) {
                throw t
            }
        })
        return arraylist
    }
}