package org.example.additional

import java.io.File

fun createNewWordsFile(fileName: String): File {
    val wordsFile = File(fileName)
    wordsFile.createNewFile()
    return wordsFile
}

fun loadDictionary(file: File): List<Word> {
    val words = mutableListOf<Word>()
    for (lines in file.readLines()) {
        val line = lines.split("|")
        val newWord = Word(original = line[0], translate = line[1], correctAnswersCount = line[2].toIntOrNull() ?: 0)
        words.add(newWord)
    }
    return words
}