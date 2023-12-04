package com.tdtu.finalproject.model.topic

import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

data class TopicWithTimeAdded (
    val topic: Topic,
    val timeAdded: String
)