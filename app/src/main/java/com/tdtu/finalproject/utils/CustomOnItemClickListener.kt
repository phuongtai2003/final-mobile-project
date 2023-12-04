package com.tdtu.finalproject.utils

import com.tdtu.finalproject.model.folder.Folder
import com.tdtu.finalproject.model.topic.Topic

interface CustomOnItemClickListener {
    fun onTopicClick(topic: Topic)
    fun onFolderClick(folder: Folder)
}