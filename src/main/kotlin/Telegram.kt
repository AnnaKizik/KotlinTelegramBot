package org.example

const val START = "/start"
const val LEARN_WORDS_CLICKED = "learn_words_clicked"
const val STATISTICS_CLICKED = "statistics_clicked"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0
    val messageIdRegexOld: Regex = ".*\"update_id\":(\\d+), \n\"message\".*".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val messageIdRegex: Regex = "\"update_id\":(\\d+)".toRegex()
    val chatIdRegex: Regex = "\"chat\":[{]\"id\":(\\d+)".toRegex()
    val dataRegex: Regex = "\"data\":\"(.+?)\"".toRegex()

    val botService = TelegramBotService(botToken)
    val trainer = LearnWordsTrainer()
    var currentQuestion: Question? = null

    fun checkNextQuestionAndSend(
        trainer: LearnWordsTrainer,
        telegramBotService: TelegramBotService,
        chatId: Long
    ) {
        val question = trainer.getNextQuestion()
        currentQuestion = question
        if (question == null) telegramBotService.sendMessage(chatId, "Все слова в словаре выучены")
        else telegramBotService.sendQuestion(chatId, question)
    }

    while (true) {
        Thread.sleep(2000)
        val updates: String = TelegramBotService(botToken).getUpdates(updateId)
        println(updates)

        val updateIdOld = messageIdRegexOld.find(updates)?.groups?.get(1)?.value?.toInt() ?: 0
        println(updateIdOld)

        val text = messageTextRegex.find(updates)?.groups?.get(1)?.value
        println(text)

        updateId = messageIdRegex.find(updates)?.groups?.get(1)?.value?.toInt() ?: 0
        println(updateId)

        val chatId = chatIdRegex.find(updates)?.groups?.get(1)?.value?.toLong() ?: 0
        println(chatId)

        val data = dataRegex.find(updates)?.groups?.get(1)?.value
        println(data)

        if (!text.isNullOrEmpty()) {
            updateId++
        }

        if (text?.lowercase() == START) {
            botService.sendMenu(chatId)
        }

        if (data?.lowercase() == STATISTICS_CLICKED) {
            val statistics = trainer.getStatistics()
            botService.sendMessage(
                chatId,
                "Выучено ${statistics.learnedCount} из ${statistics.totalCount} слов | ${statistics.percent} %"
            )
        }

        if (data?.lowercase() == LEARN_WORDS_CLICKED) {
            checkNextQuestionAndSend(trainer, botService, chatId)
        }

        if (data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true) {
            val answerIndex = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()
            val answerCheck = trainer.checkAnswer(answerIndex)
            val message =
                if (answerCheck) "Правильно!" else "Неправильно! $text – это ${currentQuestion?.correctAnswer?.translate}"
            botService.sendMessage(chatId, message)
            checkNextQuestionAndSend(trainer, botService, chatId)
        }
    }
}