package com.tdtu.finalproject.model.topic

import com.google.gson.annotations.SerializedName
import com.tdtu.finalproject.model.topic.Topic

data class GetTopicsResponse (
    @SerializedName("topics")
    val topics: List<Topic>
)