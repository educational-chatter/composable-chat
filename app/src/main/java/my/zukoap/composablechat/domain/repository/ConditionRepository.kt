package my.zukoap.composablechat.domain.repository

import my.zukoap.composablechat.common.ChatStatus
import my.zukoap.composablechat.initialization.ChatMessageListener
import my.zukoap.composablechat.presentation.ChatInternetConnectionListener
import javax.inject.Singleton


interface ConditionRepository {
    fun setInternetConnectionListener(listener: ChatInternetConnectionListener)
    fun setMessageListener(listener: ChatMessageListener)
    fun setStatusChat(newStatus: ChatStatus)
    fun getStatusChat(): ChatStatus
    fun createSessionChat()
    fun destroySessionChat()
    fun dropChat()

    // проверка вся ли история загружена
    fun getFlagAllHistoryLoaded(): Boolean
    fun saveFlagAllHistoryLoaded(isAllHistoryLoaded: Boolean)
    fun deleteFlagAllHistoryLoaded()

    fun getCurrentReadMessageTime(): Long
    fun getCountUnreadMessages(): Int
    fun saveCurrentReadMessageTime(currentReadMessageTime: Long)
    fun saveCountUnreadMessages(countUnreadMessages: Int)
    fun deleteCurrentReadMessageTime()

    // провека наличия сообщения в бд
    suspend fun getStatusExistenceMessages(): Boolean

}