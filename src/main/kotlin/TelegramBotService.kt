package org.example

import kotlinx.serialization.json.Json
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService(private val botToken: String) {

    fun getUpdates(updateId: Long): String {
        val urlGetUpdates = "$TELEGRAM_BASE_URL/bot$botToken/getUpdates?offset=$updateId"
        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String> =
            try {
                client.send(request, HttpResponse.BodyHandlers.ofString())
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }
        return response.body()
    }

    fun sendMessage(chatId: Long?, text: String): String {
        val urlSendMessage =
            "$TELEGRAM_BASE_URL/bot$botToken/sendMessage?chat_id=$chatId&text=${
                URLEncoder.encode(
                    text,
                    "UTF-8"
                )
            }"
        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        val response: HttpResponse<String> =
            try {
                client.send(request, HttpResponse.BodyHandlers.ofString())
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }
        return response.body()
    }

    companion object {
        const val TELEGRAM_BASE_URL = "https://api.telegram.org"
    }

    fun sendMenu(json: Json, chatId: Long?): String {
        val sendMessage = "$TELEGRAM_BASE_URL/bot$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = "Основное меню",
            replyMarkup = ReplyMarkup(
                listOf(
                    listOf(
                        InlineKeyboard(text = "Изучить слова", callbackData = LEARN_WORDS_CLICKED),
                        InlineKeyboard(text = "Статистика", callbackData = STATISTICS_CLICKED)
                    )
                )
            )
        )
        val requestBodyString = json.encodeToString(requestBody)

        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(sendMessage))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response: HttpResponse<String> =
            try {
                client.send(request, HttpResponse.BodyHandlers.ofString())
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }
        return response.body()
    }

    fun sendQuestion(json: Json, chatId: Long?, question: Question): String {
        val sendMessage = "$TELEGRAM_BASE_URL/bot$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = question.correctAnswer.original,
            replyMarkup = ReplyMarkup(
                listOf(
                    question.variants.mapIndexed { index, word ->
                        InlineKeyboard(
                            text = word.translate, callbackData = "$CALLBACK_DATA_ANSWER_PREFIX$index"
                        )
                    }
                )
            )
        )
        val requestBodyString = json.encodeToString(requestBody)
        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(sendMessage))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response: HttpResponse<String> =
            try {
                client.send(request, HttpResponse.BodyHandlers.ofString())
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }
        return response.body()
    }

}