package com.tdtu.finalproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tdtu.finalproject.model.User

class HomeDataViewModel : ViewModel() {
    private var user: MutableLiveData<User>? = MutableLiveData<User>()

    fun getUser(): LiveData<User>? {
        return user
    }

    public fun setUser(user: User) {
        this.user?.value = user
    }
}