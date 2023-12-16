package com.tdtu.finalproject.model.vocabulary_statistics

import com.tdtu.finalproject.model.vocabulary.Vocabulary

data class StatisticResponse (
    val vocabulary: Vocabulary,
    val statistic: VocabularyStatistics
)