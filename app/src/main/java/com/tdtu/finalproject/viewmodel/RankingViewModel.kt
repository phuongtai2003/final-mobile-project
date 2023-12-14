package com.tdtu.finalproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tdtu.finalproject.model.learning_statistics.LearningStatistic

class RankingViewModel : ViewModel() {
    private var statisticsList: MutableLiveData<List<LearningStatistic>> = MutableLiveData()
    private var originalStatisticList : MutableLiveData<List<LearningStatistic>> = MutableLiveData()

    fun getStatistics(): MutableLiveData<List<LearningStatistic>> {
        return statisticsList
    }

    fun setStatisticsList(statistics: List<LearningStatistic>) {
        statisticsList.value = statistics
    }

    fun setOriginalStatisticList(statistics: List<LearningStatistic>){
        originalStatisticList.value = statistics
    }

    fun getOriginalStatisticList(): MutableLiveData<List<LearningStatistic>>{
        return originalStatisticList
    }

    fun sortByLearningCount() {
        if(statisticsList.value != null){
            statisticsList.value = originalStatisticList.value
            val sorted = statisticsList.value?.sortedWith(compareBy<LearningStatistic> { -it.learningCount }.thenBy { it.learningPercentage * 100 })
            statisticsList.value = sorted
        }
    }

    fun sortByProgress(){
        if(statisticsList.value != null){
            statisticsList.value = originalStatisticList.value
            val sorted = statisticsList.value?.sortedWith(compareBy<LearningStatistic> { -it.learningPercentage }.thenBy { it.learningCount })
            statisticsList.value = sorted
        }
    }
    fun sortByVocabularyLearned(){
        if(statisticsList.value != null){
            statisticsList.value = originalStatisticList.value
            val sorted = statisticsList.value?.sortedWith(compareBy<LearningStatistic> { -it.vocabLearned }.thenBy { -it.learningPercentage * 100 })
            statisticsList.value = sorted
        }
    }
    fun sortByTimeLearned(){
        if(statisticsList.value != null){
            statisticsList.value = originalStatisticList.value
            val completedList = statisticsList.value?.filter { it.learningPercentage == 1.0 }
            val sorted = completedList?.sortedWith(compareBy<LearningStatistic> { it.learningTime }.thenBy { -it.learningPercentage * 100 })
            statisticsList.value = sorted
        }
    }
}