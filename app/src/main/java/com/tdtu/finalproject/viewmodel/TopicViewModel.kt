package com.tdtu.finalproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tdtu.finalproject.model.vocabulary.Vocabulary

class TopicViewModel : ViewModel() {
    private var vocabularyList: MutableLiveData<List<Vocabulary>> = MutableLiveData()

    fun getVocabularies(): MutableLiveData<List<Vocabulary>> {
        return vocabularyList
    }

    fun setVocabulariesList(vocabularies: List<Vocabulary>) {
        vocabularyList.value = vocabularies
    }
}