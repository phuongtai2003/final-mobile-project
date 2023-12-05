package com.tdtu.finalproject.model.vocabulary

import com.google.gson.annotations.SerializedName

data class GetVocabulariesByTopicResponse (
    @SerializedName("vocabularies")
    val vocabularies: List<Vocabulary>
)