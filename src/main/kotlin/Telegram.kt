package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0
    val messageIdRegexOld: Regex = ".*\"update_id\":(\\d+), \n\"message\".*".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val messageIdRegex: Regex = "\"update_id\":(\\d+)".toRegex()

    while (true) {
        Thread.sleep(2000)
        val updates: String = getUpdates(botToken, updateId)
        println(updates)

        val matchIdOldResult: MatchResult? = messageIdRegexOld.find(updates)
        val groupsIdOld = matchIdOldResult?.groups
        updateId = groupsIdOld?.get(1)?.value?.toInt() ?: 0
        println(updateId)

        val matchResult: MatchResult? = messageTextRegex.find(updates)
        val groups = matchResult?.groups
        val text = groups?.get(1)?.value
        println(text)

        val matchIdResult: MatchResult? = messageIdRegex.find(updates)
        val groupsId = matchIdResult?.groups
        updateId = groupsId?.get(1)?.value?.toInt() ?: 0
        println(updateId)
    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
    val client: HttpClient = HttpClient.newBuilder().build()
    val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

    return response.body()
}
