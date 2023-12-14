package com.tdtu.finalproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Timer
import java.util.TimerTask

class StudyViewModel :ViewModel(){
    private val _seconds = MutableLiveData<Long>()
    val seconds: MutableLiveData<Long>
        get() = _seconds

    private var timer: Timer? = null

    init {
        _seconds.value = 0
    }

    fun startTimer(){
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask(){
            override fun run() {
                _seconds.postValue(_seconds.value?.plus(1))
            }
        }, 0, 1000)
    }

    private fun stopTimer(){
        timer?.cancel()
        timer = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}