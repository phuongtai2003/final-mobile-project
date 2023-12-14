package com.tdtu.finalproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tdtu.finalproject.model.achievement.Achievement

class AchievementViewModel : ViewModel() {
    private val achievementList : MutableLiveData<List<Achievement>> = MutableLiveData()

    fun setAchievementList(list: List<Achievement>) {
        achievementList.value = list
    }

    fun getAchievementList(): MutableLiveData<List<Achievement>> {
        return achievementList
    }
}