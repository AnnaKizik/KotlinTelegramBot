package org.example.additional

import java.io.File

fun createNewWordsFile(fileName: String): File {
    val wordsFile = File(fileName)
    wordsFile.createNewFile()
    return wordsFile
}