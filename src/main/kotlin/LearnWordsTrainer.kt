package org.example

import java.io.File

const val WORDS_TO_LEARN_COUNT = 4
const val LEARNED_COUNT = 3

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

    private var question: Question? = null
    private val dictionary = loadDictionary(File("words.txt"))

    fun getStatistics(): Statistics {
        val totalCount = dictionary.size
        val learnedCount = dictionary.filter { it.correctAnswersCount >= LEARNED_COUNT }.size
        val percent = learnedCount * 100 / totalCount
        return Statistics(totalCount, learnedCount, percent)
    }

    fun getNotLearnedList(): List<Word>? {
        val notLearnedList = dictionary.filter { it.correctAnswersCount < LEARNED_COUNT }
        if (notLearnedList.isEmpty()) return null
        else return notLearnedList
    }

    fun getQuestionWords(notLearnedList: List<Word>?): List<Word>? {
        if (notLearnedList == null) {
            return null
        } else {
            var questionWords = notLearnedList.shuffled().take(WORDS_TO_LEARN_COUNT)
            if (questionWords.size < WORDS_TO_LEARN_COUNT) {
                questionWords = (questionWords + dictionary).take(WORDS_TO_LEARN_COUNT)
            }
            return questionWords
        }
    }

    fun getNextQuestion(notLearnedList: List<Word>?, questionWords: List<Word>?): Question? {
        if (questionWords == null || notLearnedList == null) return null

        val updateNotLearnedList = notLearnedList.filter { it.correctAnswersCount < LEARNED_COUNT }
        if (updateNotLearnedList.isEmpty()) return null

        val updateQuestionWords = updateNotLearnedList.shuffled().take(WORDS_TO_LEARN_COUNT)
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
                saveDictionary(dictionary, File("words.txt"))
                true
            } else {
                false
            }
        } ?: false
    }

    private fun loadDictionary(file: File): List<Word> {
        val words = mutableListOf<Word>()
        for (lines in file.readLines()) {
            val line = lines.split("|")
            val newWord =
                Word(original = line[0], translate = line[1], correctAnswersCount = line[2].toIntOrNull() ?: 0)
            words.add(newWord)
        }
        return words
    }

    private fun saveDictionary(updateDictionary: List<Word>, file: File) {
        val updateWordList = updateDictionary.joinToString("\n") { word ->
            "${word.original}|${word.translate}|${word.correctAnswersCount}"
        }
        file.writeText(updateWordList)
    }
}