package org.example

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService(private val botToken: String) {

    fun getUpdates(updateId: Int): String {
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

    fun sendMessage(chatId: Long, text: String): String {
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

    fun sendMenu(chatId: Long): String {
        val sendMessage = "$TELEGRAM_BASE_URL/bot$botToken/sendMessage"
        val sendMenuBody = """
            {"chat_id": $chatId,
            "text":  "Основное меню",
            "reply_markup": {
            "inline_keyboard": [
            [
            {
            "text": "Изучить слова",
             "callback_data": "$LEARN_WORDS_CLICKED"
            },
            {
            "text": "Статистика",
            "callback_data": "$STATISTICS_CLICKED"
            }
            ]
            ]
            }}
        """.trimIndent()
        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(sendMessage))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
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

    fun sendQuestion(chatId: Long, question: Question): String {
        val sendMessage = "$TELEGRAM_BASE_URL/bot$botToken/sendMessage"
        val variantsKeyboard = question.variants.mapIndexed { index, word ->
            """
                   {
                   "text": "${word.translate}",
                   "callback_data":"${CALLBACK_DATA_ANSWER_PREFIX}$index"}
               """.trimIndent()
        }.chunked(1) { row ->
            "[" + row.joinToString(",") + "]"
        }.joinToString(",", "[", "]")
        val sendQuestionBody = """
            {"chat_id": $chatId,
            "text":  "${question.correctAnswer.original}",
            "reply_markup": {
            "inline_keyboard": $variantsKeyboard
            }}
        """.trimIndent()
        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(sendMessage))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendQuestionBody))
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