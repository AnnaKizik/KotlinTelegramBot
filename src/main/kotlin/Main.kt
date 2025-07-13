package org.example

fun Question.asConsoleString(): String {
    val answerVariants = this.variants
        .mapIndexed { index: Int, word: Word ->
            "${index + 1} - ${word.translate}"
        }.shuffled()
        .joinToString("\n")
    return this.correctAnswer.original + "\n" + answerVariants + "\n0 - Меню"
}

fun main() {

    val trainer = LearnWordsTrainer()

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

                val notLearnedList = trainer.getNotLearnedList()
                val questionWords = trainer.getQuestionWords(notLearnedList)

                while (true) {
                    val question = trainer.getNextQuestion(notLearnedList, questionWords)
                    if (question == null) {
                        println("Все слова выучены!")
                        break
                    } else {
                        println(question.asConsoleString())
                        print("Введите номер ответа: ")
                        val userAnswerInput = readln().toIntOrNull()
                        if (userAnswerInput == 0) break

                        if (trainer.checkAnswer(userAnswerInput?.minus(1))) {
                            println("Правильно!")
                        } else {
                            println("Неправильно! ${question.correctAnswer.original} – это ${question.correctAnswer.translate}")
                        }
                    }
                }
            }

            "2" -> {
                println(
                    "Выбран раздел \"Статистика\""
                )
                val statistics = trainer.getStatistics()
                println("Выучено ${statistics.learnedCount} из ${statistics.totalCount} слов | ${statistics.percent}%\n")
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