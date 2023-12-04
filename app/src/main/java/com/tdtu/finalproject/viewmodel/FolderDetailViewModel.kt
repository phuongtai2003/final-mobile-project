package com.tdtu.finalproject.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tdtu.finalproject.FolderDetailActivity
import com.tdtu.finalproject.R
import com.tdtu.finalproject.databinding.ActivityFolderDetailBinding
import com.tdtu.finalproject.model.folder.Folder
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.repository.DataRepository
import com.tdtu.finalproject.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FolderDetailViewModel : ViewModel() {
    fun updateTopicForFolder( dataRepository: DataRepository,binding: ActivityFolderDetailBinding,mContext: Context,addedTopic: List<Topic>, removedTopic: List<Topic>, folder: Folder, sharedPreferences: SharedPreferences){
        viewModelScope.launch {
            for (topic in addedTopic){
                try {
                    dataRepository.addTopicToFolder(
                        folder.id!!, topic.id!!, sharedPreferences.getString(
                            mContext.getString(
                                R.string.token_key
                            ), null
                        )!!
                    ).await()
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Utils.showSnackBar(binding.root, e.message.toString())
                    }
                }
            }
            for (topic in removedTopic){
                try{
                    dataRepository.deleteTopicFromFolder(folder.id!!, topic.id!!, sharedPreferences.getString(mContext.getString(
                        R.string.token_key), null)!!).await()
                }catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Utils.showSnackBar(binding.root, e.message.toString())
                    }
                }
            }
        }
    }
}