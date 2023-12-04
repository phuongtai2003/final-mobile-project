package com.tdtu.finalproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tdtu.finalproject.model.folder.Folder
import com.tdtu.finalproject.model.topic.Topic
import com.tdtu.finalproject.model.user.User

class HomeDataViewModel : ViewModel() {
    private var user: MutableLiveData<User>? = MutableLiveData<User>()
    private var topicsList: MutableLiveData<List<Topic>>? = MutableLiveData<List<Topic>>()
    private var folderList: MutableLiveData<List<Folder>>? = MutableLiveData<List<Folder>>()

    init {

    }
    fun getUser(): LiveData<User>? {
        return user
    }

    fun setUser(user: User) {
        this.user?.value = user
    }

    fun getTopicsList(): LiveData<List<Topic>>?{
        return topicsList
    }

    fun setTopicsList(topicsList: List<Topic>){
        this.topicsList?.value = topicsList
    }

    fun getFolderList(): LiveData<List<Folder>>?{
        return folderList
    }

    fun setFolderList(folderList: List<Folder>){
        this.folderList?.value = folderList
    }
}