package org.example.additional

import java.io.File

fun main() {
    val wordsFile = File("words.txt")
    wordsFile.createNewFile()

    val lines: List<String> = wordsFile.readLines()
    val dictionary = mutableListOf<Word>()

    for (line in lines) {
        val line = line.split("|")
        val newWord = Word(original = line[0], translate = line[1], correctAnswersCount = line[2].toIntOrNull() ?: 0)
        dictionary.add(newWord)
    }

    println(dictionary)
}