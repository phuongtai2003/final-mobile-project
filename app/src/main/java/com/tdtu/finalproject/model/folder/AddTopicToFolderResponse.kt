package com.tdtu.finalproject.model.folder

import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

data class AddTopicToFolderResponse (
    val message: String,
    val folder: Folder,
    val dateTimeAdded: Object
)