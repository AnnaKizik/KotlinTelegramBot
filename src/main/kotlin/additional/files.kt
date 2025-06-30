package org.example.additional

import java.io.File

fun main() {
    val wordsFile = File("words.txt")
    wordsFile.createNewFile()

    val dictionary = loadDictionary(wordsFile.readLines())
    println(dictionary)
}

fun loadDictionary(lines: List<String>): MutableList<Word> {
    val words = mutableListOf<Word>()
    for (i in lines) {
        val line = i.split("|")
        val newWord = Word(original = line[0], translate = line[1], correctAnswersCount = line[2].toIntOrNull() ?: 0)
        words.add(newWord)
    }
    return words
}