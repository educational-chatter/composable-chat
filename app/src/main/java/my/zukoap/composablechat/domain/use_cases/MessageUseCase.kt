package my.zukoap.composablechat.domain.use_cases

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import kotlinx.coroutines.CoroutineDispatcher
import my.zukoap.composablechat.data.local.db.entity.MessageEntity
import my.zukoap.composablechat.data.paging.ChatRemoteMediator
import my.zukoap.composablechat.domain.repository.ConditionRepository
import my.zukoap.composablechat.domain.repository.MessageRepository

class MessageUseCase(
    private val messageRepository: MessageRepository,
    private val conditionRepository: ConditionRepository,
    private val visitorUseCase: VisitorUseCase,
    private val personUseCase: PersonUseCase,
) {

    fun getAllMessages(): PagingSource<Int, MessageEntity> =
        messageRepository.getMessages()

    fun getCountUnreadMessages(currentReadMessageTime: Long, timestampLastMessage: Long?): Int? {
        return if (timestampLastMessage == null) {
            messageRepository.getCountUnreadMessages(currentReadMessageTime)
        } else {
            messageRepository.getCountUnreadMessagesRange(
                currentReadMessageTime,
                timestampLastMessage
            )
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getPager(ioDispatcher: CoroutineDispatcher): Pager<Int, MessageEntity> {
        val countUnreadMessages = conditionRepository.getCountUnreadMessages()
        val visitor = visitorUseCase.getVisitor()!!
        ChatRemoteMediator(
            conditionRepository,
            personUseCase,
            messageRepository,
            ioDispatcher,
            visitor
        )
        return Pager(
            config = PagingConfig(20, enablePlaceholders = true, prefetchDistance = 30),
            initialKey = if (countUnreadMessages > 0) countUnreadMessages - 1 else 0,
            remoteMediator = ChatRemoteMediator(
                conditionRepository,
                personUseCase,
                messageRepository,
                ioDispatcher,
                visitor
            ),
            pagingSourceFactory = { messageRepository.getMessages() }
        )
    }

    fun getCountMessagesInclusiveTimestampById(messageId: String): Int? {
        return messageRepository.getTimestampMessageById(messageId)
            ?.run(messageRepository::getCountMessagesInclusiveTimestamp)
    }

    suspend fun sendMessage(message: String, repliedMessageId: String?) {
        messageRepository.sendMessages(message, repliedMessageId)
    }

    suspend fun selectActionInMessage(messageId: String, actionId: String) {
        messageRepository.selectAction(messageId, actionId)
    }

    suspend fun uploadHistoryMessages(
        eventAllHistoryLoaded: () -> Unit,
        uploadHistoryComplete: () -> Unit,
        executeAnyway: Boolean
    ) {
        val visitor = visitorUseCase.getVisitor() ?: return
        val statusExistenceMessages = conditionRepository.getStatusExistenceMessages()
        val flagAllHistoryLoaded = conditionRepository.getFlagAllHistoryLoaded()

        when {
            !statusExistenceMessages && executeAnyway -> uploadHistoryComplete()
            statusExistenceMessages && (!flagAllHistoryLoaded || executeAnyway) -> messageRepository
                .getTimeFirstMessage()
                ?.let { firstMessageTime ->
                    messageRepository.uploadMessages(
                        uuid = visitor.uuid,
                        startTime = null,
                        endTime = firstMessageTime,
                        updateReadPoint = { false },
                        syncMessagesAcrossDevices = {},
                        returnedEmptyPool = {
                            eventAllHistoryLoaded()
                            conditionRepository.saveFlagAllHistoryLoaded(true)
                        },
                        getPersonPreview = { personId ->
                            personUseCase.getPersonPreview(personId, visitor.token)
                        },
                        getFileInfo = messageRepository::getFileInfo
                    )
                    uploadHistoryComplete()
                }
        }
    }

    // при переходе на холд добавить вызов метода, обновляющего состояния у сообщений, находящихся в статусе "отправляется"
    suspend fun syncMessages(
        updateReadPoint: (newTimeMark: Long) -> Boolean,
        syncMessagesAcrossDevices: (indexFirstUnreadMessage: Int) -> Unit,
        eventAllHistoryLoaded: () -> Unit
    ) {
        val visitor = visitorUseCase.getVisitor() ?: return
        val syncMessagesAcrossDevicesWrapper: (countUnreadMessages: Int) -> Unit =
            { countUnreadMessages ->
                syncMessagesAcrossDevices(
                    if (countUnreadMessages > 0) countUnreadMessages - 1
                    else 0
                )
            }

        if (conditionRepository.getStatusExistenceMessages()) {
            messageRepository.getTimeLastMessage()?.let { lastMessageTime ->
                val messages = messageRepository.uploadMessages(
                    uuid = visitor.uuid,
                    startTime = lastMessageTime + 1,
                    endTime = 0,
                    updateReadPoint = updateReadPoint,
                    syncMessagesAcrossDevices = syncMessagesAcrossDevicesWrapper,
                    returnedEmptyPool = {},
                    getPersonPreview = { personId ->
                        personUseCase.getPersonPreview(personId, visitor.token)
                    },
                    getFileInfo = messageRepository::getFileInfo
                )
                messageRepository.updatePersonNames(messages, personUseCase::updatePersonName)
                messageRepository.mergeNewMessages()
            }
        } else {
//            if (remoteReadMessageTime == 0L) {
            val messages = messageRepository.uploadMessages(
                uuid = visitor.uuid,
                startTime = null,
                endTime = 0,
                updateReadPoint = updateReadPoint,
                syncMessagesAcrossDevices = syncMessagesAcrossDevices,
                returnedEmptyPool = {
                    eventAllHistoryLoaded()
                    conditionRepository.saveFlagAllHistoryLoaded(true)
                },
                getPersonPreview = { personId ->
                    personUseCase.getPersonPreview(personId, visitor.token)
                },
                getFileInfo = messageRepository::getFileInfo
            )
            messageRepository.updatePersonNames(messages, personUseCase::updatePersonName)
            messageRepository.mergeNewMessages()
//            } else {
//                val messages = messageRepository.uploadMessages(
//                    uuid = visitor.uuid,
//                    token = visitor.token,
//                    startTime = remoteReadMessageTime,
//                    endTime = 0,
//                    updateReadPoint = updateReadPoint,
//                    syncMessagesAcrossDevices = syncMessagesAcrossDevices,
//                    returnedEmptyPool = {},
//                    getPersonPreview = { personId ->
//                        personUseCase.getPersonPreview(personId, visitor.token)
//                    },
//                    getFileInfo = messageRepository::getFileInfo
//                )
//                messageRepository.updatePersonNames(messages, personUseCase::updatePersonName)
//                messageRepository.mergeNewMessages()
//            }
        }
    }

    fun updateSizeMessage(id: String, height: Int, width: Int) {
        messageRepository.updateSizeMessage(id, height, width)
    }

    fun removeAllInfoMessages() {
        messageRepository.removeAllInfoMessages()
    }

}