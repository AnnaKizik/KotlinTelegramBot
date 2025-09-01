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

    while (true) {
        Thread.sleep(2000)
        val updates: String = TelegramBotService(botToken).getUpdates(updateId)
        println(updates)

        val matchIdOldResult: MatchResult? = messageIdRegexOld.find(updates)
        val groupsIdOld = matchIdOldResult?.groups
        updateIdOld = groupsIdOld?.get(1)?.value?.toInt() ?: 0
        println(updateIdOld)

        val matchResult: MatchResult? = messageTextRegex.find(updates)
        val groups = matchResult?.groups
        val text = groups?.get(1)?.value
        println(text)

        val matchIdResult: MatchResult? = messageIdRegex.find(updates)
        val groupsId = matchIdResult?.groups
        updateId = groupsId?.get(1)?.value?.toInt() ?: 0
        println(updateId)

        val matchChatIdResult: MatchResult? = chatIdRegex.find(updates)
        val groupsChatId = matchChatIdResult?.groups
        chatId = groupsChatId?.get(1)?.value?.toLong() ?: 0
        println(chatId)

        if (!text.isNullOrEmpty() && chatId != 0.toLong()) {
            val response = TelegramBotService(botToken).sendMessage(
                chatId,
                text
            )
            println("Ответ отправлен: $response")
            updateId++
        }
    }
}