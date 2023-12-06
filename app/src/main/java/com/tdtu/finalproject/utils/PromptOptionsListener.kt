package com.tdtu.finalproject.utils

interface PromptOptionsListener {
    fun onChoosingOption(answerByDefinition: Boolean, answerByVocabulary: Boolean, questionByDefinition: Boolean, questionByVocabulary: Boolean)
}