package com.tdtu.finalproject.utils

import com.tdtu.finalproject.model.folder.Folder

interface OnTopicDialogListener {
    fun onSaveToFolder()
    fun onDeleteTopic()
    fun onEditTopic()
}