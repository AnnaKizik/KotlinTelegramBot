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

class LearnWordsTrainer(
    private val fileName: String = "words.txt"
) {
    private val wordsToLearnCount = 4
    private val learnedCount = 3
    var currentQuestion: Question? = null
    private val dictionary = loadDictionary()

    fun getStatistics(): Statistics {
        val totalCount = dictionary.size
        val learnedCount = dictionary.filter { it.correctAnswersCount >= learnedCount }.size
        val percent = learnedCount * 100 / totalCount
        return Statistics(totalCount, learnedCount, percent)
    }

    fun getNextQuestion(): Question? {
        val notLearnedList = getNotLearnedList()
        if (notLearnedList.isEmpty()) return null

        var questionWords = notLearnedList.shuffled().take(wordsToLearnCount)

        if (questionWords.size < wordsToLearnCount) {
            questionWords = (questionWords + dictionary.shuffled().take(wordsToLearnCount - questionWords.size))
                .distinct()
                .take(wordsToLearnCount)
                .shuffled()
        }

        val correctAnswer = questionWords.random()
        currentQuestion = Question(questionWords, correctAnswer)
        return currentQuestion
    }

    fun checkAnswer(userAnswerIndex: Int?): Boolean {
        return currentQuestion?.let {
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

    private fun getNotLearnedList(): List<Word> = dictionary.filter { it.correctAnswersCount < learnedCount }

    private fun loadDictionary(): List<Word> {
        try {
            val wordsFile = File(fileName)
            if (!wordsFile.exists()) {
                File("words.txt").copyTo(wordsFile)
            }
            val words = mutableListOf<Word>()
            for (lines in wordsFile.readLines()) {
                val line = lines.split("|")
                val newWord =
                    Word(original = line[0], translate = line[1], correctAnswersCount = line[2].toIntOrNull() ?: 0)
                words.add(newWord)
            }
            return words
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalStateException("Некорректный файл", e)
        }
    }

    private fun saveDictionary() {
        val updateWordList = dictionary.joinToString("\n") { word ->
            "${word.original}|${word.translate}|${word.correctAnswersCount}"
        }
        File(fileName).writeText(updateWordList)
    }

    fun resetProgress() {
        dictionary.forEach { it.correctAnswersCount = 0 }
        saveDictionary()
    }
}