package com.tdtu.finalproject.model.topic

import com.google.gson.annotations.SerializedName
import com.tdtu.finalproject.model.topic.Topic
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

data class GetTopicsResponse (
    @SerializedName("topics")
    val topics: List<Topic>,
    @SerializedName("timeAdded")
    val timeAdded: List<JvmType.Object>?
)