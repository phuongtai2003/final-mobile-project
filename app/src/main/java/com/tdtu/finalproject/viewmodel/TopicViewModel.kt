package com.tdtu.finalproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tdtu.finalproject.model.vocabulary.Vocabulary
import com.tdtu.finalproject.model.vocabulary_statistics.StatisticResponse
import com.tdtu.finalproject.model.vocabulary_statistics.VocabularyStatistics

class TopicViewModel : ViewModel() {
    private var vocabularyList: MutableLiveData<List<Vocabulary>> = MutableLiveData()
    private var statisticList: MutableLiveData<List<StatisticResponse>> = MutableLiveData()

    fun getVocabularies(): MutableLiveData<List<Vocabulary>> {
        return vocabularyList
    }

    fun setVocabulariesList(vocabularies: List<Vocabulary>) {
        vocabularyList.value = vocabularies
    }

    fun getStatistics(): MutableLiveData<List<StatisticResponse>> {
        return statisticList
    }

    fun setStatisticsList(statistics: List<StatisticResponse>) {
        statisticList.value = statistics
    }
}