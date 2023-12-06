package com.tdtu.finalproject.model.quizzes

import com.tdtu.finalproject.model.vocabulary.Vocabulary

data class Quiz(
    var correctAnswer: Vocabulary,
    var wrongAnswer: List<Vocabulary>
)