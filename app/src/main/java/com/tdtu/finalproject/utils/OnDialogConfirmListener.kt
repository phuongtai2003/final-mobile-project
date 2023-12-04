package com.tdtu.finalproject.utils

import com.tdtu.finalproject.model.topic.Topic

interface OnDialogConfirmListener {
    fun onCreateFolderDialogConfirm(folderNameEnglish: String, folderNameVietnamese: String)
    fun onAddTopicToFolderDialogConfirm()
    fun onDeleteFolderDialogConfirm()
}