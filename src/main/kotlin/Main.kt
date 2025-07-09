package org.example

import org.example.additional.LEARNED_COUNT
import org.example.additional.loadDictionary
import org.example.additional.saveDictionary
import java.io.File

const val WORDS_TO_LEARN_COUNT = 4

fun main() {
    val dictionary = loadDictionary(File("words.txt"))

    while (true) {
        println(
            """
            Меню:
            1 – Учить слова
            2 – Статистика
            0 – Выход
        """.trimIndent()
        )

        val userInput = readln()

        when (userInput) {
            "1" -> {
                println(
                    "Выбран раздел \"Учить слова\""
                )
                while (true) {
                    val notLearnedList = dictionary.filter { it.correctAnswersCount < LEARNED_COUNT }
                    if (notLearnedList.isEmpty()) {
                        println("Все слова выучены")
                        break
                    }
                    val questionWords = notLearnedList.shuffled().take(WORDS_TO_LEARN_COUNT)
                    val correctAnswer = questionWords.random()
                    println("\n${correctAnswer.original}:")
                    val answerOptions = questionWords.shuffled()
                    val correctAnswerId = answerOptions.indexOf(correctAnswer).toString()
                    answerOptions.forEachIndexed { index, variant ->
                        println("${index + 1} - ${variant.translate}")
                    }
                    println("----------\n0 - Меню")
                    print("Введите номер ответа: ")
                    val userAnswerInput = readln()

                    if (userAnswerInput == "0") break

                    if (correctAnswerId == (userAnswerInput.toInt() - 1).toString()) {
                        println("Правильно!")
                        correctAnswer.correctAnswersCount++
                        saveDictionary(dictionary, File("words.txt"))
                    } else {
                        println("Неправильно! ${correctAnswer.original} – это ${correctAnswer.translate}")
                    }
                }
            }

            "2" -> {
                println(
                    "Выбран раздел \"Статистика\""
                )
                val totalCount = dictionary.size
                val learnedCount = dictionary.filter { it.correctAnswersCount >= LEARNED_COUNT }.size
                val percent = learnedCount * 100 / totalCount
                println("Выучено $learnedCount из $totalCount слов | $percent%\n")
                continue
            }

            "0" -> return
            else -> {
                println("Выберите число 1, 2 или 0")
                continue
            }
        }
    }
}