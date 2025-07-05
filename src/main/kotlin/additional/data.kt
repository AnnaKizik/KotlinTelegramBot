package org.example.additional

const val LEARNED_COUNT = 3

data class Word(
    val original: String,
    val translate: String,
    val correctAnswersCount: Int = 0
)