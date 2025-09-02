package org.example

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateIdOld = 0
    var updateId = 0
    var chatId: Long
    val messageIdRegexOld: Regex = ".*\"update_id\":(\\d+), \n\"message\".*".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val messageIdRegex: Regex = "\"update_id\":(\\d+)".toRegex()
    val chatIdRegex: Regex = "\"chat\":[{]\"id\":(\\d+)".toRegex()
    val dataRegex: Regex = "\"data\":\"(.+?)\"".toRegex()

    val trainer = LearnWordsTrainer()

    while (true) {
        Thread.sleep(2000)
        val updates: String = TelegramBotService(botToken).getUpdates(updateId)
        println(updates)

        updateIdOld = messageIdRegexOld.find(updates)?.groups?.get(1)?.value?.toInt() ?: 0
        println(updateIdOld)

        val text = messageTextRegex.find(updates)?.groups?.get(1)?.value
        println(text)

        updateId = messageIdRegex.find(updates)?.groups?.get(1)?.value?.toInt() ?: 0
        println(updateId)

        chatId = chatIdRegex.find(updates)?.groups?.get(1)?.value?.toLong() ?: 0
        println(chatId)

        val data = dataRegex.find(updates)?.groups?.get(1)?.value
        println(data)

        if (!text.isNullOrEmpty() && chatId != 0.toLong()) {
            val response = TelegramBotService(botToken).sendMessage(
                chatId,
                text
            )
            println("Ответ отправлен: $response")
            updateId++
        }

        if (text?.lowercase() == "/start" && chatId != null) {
            TelegramBotService(botToken).sendMenu(chatId)
        }

        if (data?.lowercase() == "statistics_clicked" && chatId != null) {
            TelegramBotService(botToken).sendMessage(chatId, "Выучено 10 из 10 слов")
        }
    }
}