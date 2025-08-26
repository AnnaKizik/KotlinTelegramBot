package org.example

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateIdOld = 0
    var updateId = 0
    var chatId = 0
    val messageIdRegexOld: Regex = ".*\"update_id\":(\\d+), \n\"message\".*".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val messageIdRegex: Regex = "\"update_id\":(\\d+)".toRegex()
    val chatIdRegex: Regex = "\"chat\":[{]\"id\":(\\d+)".toRegex()

    while (true) {
        Thread.sleep(2000)
        val updates: String = getUpdates(botToken, updateId)
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
        chatId = groupsChatId?.get(1)?.value?.toInt() ?: 0
        println(chatId)

        if (text == "Hello" && chatId != 0) {
            val response = sendMessage(botToken, chatId, "Hello")
            println("Ответ отправлен: $response")
            updateId++
        }
    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
    val client: HttpClient = HttpClient.newBuilder().build()
    val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
    return response.body()
}

fun sendMessage(botToken: String, chatId: Int, messageText: String): String {
    val urlSendMessage =
        "https://api.telegram.org/bot$botToken/sendMessage?chat_id=$chatId&text=${URLEncoder.encode(messageText, "UTF-8")}"
    val client: HttpClient = HttpClient.newBuilder().build()
    val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
    val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
    return response.body()
}