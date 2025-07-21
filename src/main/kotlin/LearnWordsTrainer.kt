package org.example

import java.io.File

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0
)

data class Statistics(
    val totalCount: Int,
    val learnedCount: Int,
    val percent: Int,
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer {
    private val wordsToLearnCount = 4
    private val learnedCount = 3
    private var question: Question? = null
    private val dictionary = loadDictionary()

    fun getStatistics(): Statistics {
        val totalCount = dictionary.size
        val learnedCount = dictionary.filter { it.correctAnswersCount >= learnedCount }.size
        val percent = learnedCount * 100 / totalCount
        return Statistics(totalCount, learnedCount, percent)
    }

    fun getNextQuestion(): Question? {
        val notLearnedList = getNotLearnedList()
        val questionWords = getQuestionWords(notLearnedList)

        if (questionWords == null || notLearnedList == null) return null

        val updateNotLearnedList = notLearnedList.filter { it.correctAnswersCount < learnedCount }
        if (updateNotLearnedList.isEmpty()) return null

        val updateQuestionWords = updateNotLearnedList.shuffled().take(wordsToLearnCount)
        if (updateQuestionWords.isEmpty()) return null

        val correctAnswer = updateQuestionWords.random()
        question = Question(questionWords, correctAnswer)
        return question
    }

    fun checkAnswer(userAnswerIndex: Int?): Boolean {
        return question?.let {
            val correctAnswerId = it.variants.indexOf(it.correctAnswer)
            if (correctAnswerId == userAnswerIndex) {
                it.correctAnswer.correctAnswersCount++
                saveDictionary()
                true
            } else {
                false
            }
        } ?: false
    }

    private fun getNotLearnedList(): List<Word>? {
        val notLearnedList = dictionary.filter { it.correctAnswersCount < learnedCount }
        if (notLearnedList.isEmpty()) return null
        else return notLearnedList
    }

    private fun getQuestionWords(notLearnedList: List<Word>?): List<Word>? {
        if (notLearnedList == null) {
            return null
        } else {
            var questionWords = notLearnedList.shuffled().take(wordsToLearnCount)
            if (questionWords.size < wordsToLearnCount) {
                questionWords = (questionWords + dictionary).distinct().take(wordsToLearnCount).shuffled()
            }
            return questionWords
        }
    }

    private fun loadDictionary(): List<Word> {
        try {
            val words = mutableListOf<Word>()
            for (lines in File("words.txt").readLines()) {
                val line = lines.split("|")
                val newWord =
                    Word(original = line[0], translate = line[1], correctAnswersCount = line[2].toIntOrNull() ?: 0)
                words.add(newWord)
            }
            return words
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalStateException("Некорректный файл")
        }
    }

    private fun saveDictionary() {
        val updateWordList = dictionary.joinToString("\n") { word ->
            "${word.original}|${word.translate}|${word.correctAnswersCount}"
        }
        File("words.txt").writeText(updateWordList)
    }
}