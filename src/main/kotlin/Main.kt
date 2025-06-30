package org.example

fun main() {

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

            "2" -> println(
                "Выбран раздел \"Статистика\""
            )

            "0" -> return
            else -> {
                println("Выберите число 1, 2 или 0")
                continue
            }
        }
    }

}