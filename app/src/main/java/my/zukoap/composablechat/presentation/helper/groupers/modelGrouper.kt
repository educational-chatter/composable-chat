package my.zukoap.composablechat.presentation.helper.groupers

import android.annotation.SuppressLint
import my.zukoap.composablechat.presentation.chat.model.MessageModel
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
fun groupPageByDate(messages: List<MessageModel>): List<MessageModel> {
    messages.forEach {
        it.isFirstMessageInDay = false
    }

    val formatTime = SimpleDateFormat("dd.MM.yyyy")
    val listWithDate = messages.map { formatTime.format(it.timestamp) }

    listWithDate.toSet().forEach { date ->
        val index = listWithDate.indexOfLast { it == date }
        if (index != -1) {
            messages[index].isFirstMessageInDay = true
        }
    }

    return messages
}