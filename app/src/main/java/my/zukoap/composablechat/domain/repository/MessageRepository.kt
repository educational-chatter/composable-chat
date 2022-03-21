package my.zukoap.composablechat.domain.repository

import android.content.Context
import androidx.paging.DataSource
import androidx.paging.PagingSource
import my.zukoap.composablechat.data.local.db.entity.MessageEntity
import my.zukoap.composablechat.domain.entity.file.TypeDownloadProgress
import my.zukoap.composablechat.domain.entity.message.NetworkMessage
import my.zukoap.composablechat.domain.transfer.TransferFileInfo


interface MessageRepository {

    fun getMessages(): PagingSource<Int, MessageEntity>

    fun getCountUnreadMessages(currentReadMessageTime: Long): Int?

    fun getTimestampMessageById(messageId: String): Long?

    fun getCountMessagesInclusiveTimestamp(timestampMessage: Long): Int?

    fun getCountUnreadMessagesRange(currentReadMessageTime: Long, timestampLastMessage: Long): Int?

    // получение времени первого сообщения
    suspend fun getTimeFirstMessage(): Long?

    // получение времени последнего сообщения
    suspend fun getTimeLastMessage(): Long?

    // загрузка определенного пула сообщений
    suspend fun uploadMessages(
        uuid: String,
        startTime: Long?,
        endTime: Long,
        updateReadPoint: (newTimeMark: Long) -> Boolean,
        syncMessagesAcrossDevices: (countUnreadMessages: Int) -> Unit,
        returnedEmptyPool: () -> Unit,
        getPersonPreview: suspend (personId: String) -> String?,
        getFileInfo: suspend (context: Context, networkMessage: NetworkMessage) -> TransferFileInfo?
    ): List<MessageEntity>

    suspend fun mergeNewMessages()

    suspend fun updatePersonNames(
        messages: List<MessageEntity>,
        updatePersonName: suspend (personId: String?, currentPersonName: String?) -> Unit
    )

    suspend fun getFileInfo(
        context: Context,
        networkMessage: NetworkMessage
    ): TransferFileInfo?

    suspend fun sendMessages(message: String, repliedMessageId: String?)
    suspend fun selectAction(messageId: String, actionId: String)

    fun updateSizeMessage(id: String, height: Int, width: Int)

    fun updateTypeDownloadProgressOfMessageWithAttachment(id: String, typeDownloadProgress: TypeDownloadProgress)

    fun removeAllInfoMessages()

}