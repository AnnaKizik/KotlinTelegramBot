package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val START = "/start"
const val LEARN_WORDS_CLICKED = "learn_words_clicked"
const val STATISTICS_CLICKED = "statistics_clicked"
const val RESET_CLICKED = "reset_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"
const val TO_MENU = "to_menu"

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>,
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String,
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String? = null,
    @SerialName("message")
    val message: Message? = null,
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long,
)

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId: Long?,
    @SerialName("text")
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup? = null,
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboard>>,
)

@Serializable
data class InlineKeyboard(
    @SerialName("callback_data")
    val callbackData: String,
    @SerialName("text")
    val text: String,
)

fun main(args: Array<String>) {

    val botToken = args[0]
    var lastUpdateId = 0L
    val botService = TelegramBotService(botToken)
    val trainers = HashMap<Long, LearnWordsTrainer>()
    val json = Json {
        ignoreUnknownKeys = true
    }

    fun checkNextQuestionAndSend(
        trainer: LearnWordsTrainer,
        telegramBotService: TelegramBotService,
        chatId: Long?
    ) {
        val question = trainer.getNextQuestion()
        trainer.currentQuestion = question
        if (question == null) telegramBotService.sendMessage(chatId, "Все слова в словаре выучены")
        else telegramBotService.sendQuestion(json, chatId, question)
    }

    fun handleUpdate(update: Update, json: Json, trainers: HashMap<Long, LearnWordsTrainer>) {
        val message = update.message?.text
        val chatId = update.message?.chat?.id ?: update.callbackQuery?.message?.chat?.id ?: return
        val data = update.callbackQuery?.data

        val trainer = trainers.getOrPut(chatId) {
            LearnWordsTrainer("$chatId.txt")
        }

        if (message?.lowercase() == START) {
            botService.sendMenu(json, chatId)
        }

        if (data?.lowercase() == STATISTICS_CLICKED) {
            val statistics = trainer.getStatistics()
            botService.sendMessage(
                chatId,
                "Выучено ${statistics.learnedCount} из ${statistics.totalCount} слов | ${statistics.percent} %"
            )
        }

        if (data?.lowercase() == RESET_CLICKED) {
            trainer.resetProgress()
            botService.sendMessage(
                chatId,
                "Прогресс сброшен"
            )
        }

        if (data?.lowercase() == LEARN_WORDS_CLICKED) {
            checkNextQuestionAndSend(trainer, botService, chatId)
        }

        if (data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true) {
            val answerIndex = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()
            val answerCheck = trainer.checkAnswer(answerIndex)
            val messageText =
                if (answerCheck) "Правильно!" else "Неправильно! ${trainer.currentQuestion?.correctAnswer?.original} – это ${trainer.currentQuestion?.correctAnswer?.translate}"
            botService.sendMessage(chatId, messageText)
            checkNextQuestionAndSend(trainer, botService, chatId)
        }

        if (data?.lowercase() == TO_MENU) {
            botService.sendMenu(json, chatId)
        }
    }

    while (true) {
        Thread.sleep(2000)
        val responseString: String = TelegramBotService(botToken).getUpdates(lastUpdateId)
        println(responseString)

        val response: Response = json.decodeFromString(responseString)
        if (response.result.isEmpty()) continue
        val sortedUpdates = response.result.sortedBy { it.updateId }
        sortedUpdates.forEach { handleUpdate(it, json, trainers) }
        lastUpdateId = sortedUpdates.last().updateId + 1

    }

}