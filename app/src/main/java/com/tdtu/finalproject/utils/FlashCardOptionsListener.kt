package com.tdtu.finalproject.utils

interface FlashCardOptionsListener {
    fun onApply(isShuffle: Boolean, isAutoPlaySound: Boolean, isFrontFirst: Boolean, studyLanguage: Language)
}