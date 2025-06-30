package org.example.additional

data class Word(
    val original: String,
    val translate: String,
    val correctAnswersCount: Int? = 0
)