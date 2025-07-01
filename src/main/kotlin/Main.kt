package org.example

import org.example.additional.loadDictionary
import java.io.File

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
            "1" -> println(
                "Выбран раздел \"Учить слова\""
            )

            "2" -> {
                println(
                    "Выбран раздел \"Статистика\""
                )
                val totalCount = dictionary.size
                val learnedCount = dictionary.filter { it.correctAnswersCount!! >= 3 }.size
                val percent = (learnedCount / totalCount) * 100
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