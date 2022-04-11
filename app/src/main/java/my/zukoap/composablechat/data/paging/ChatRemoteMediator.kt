package my.zukoap.composablechat.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import my.zukoap.composablechat.data.local.db.entity.MessageEntity
import my.zukoap.composablechat.domain.entity.auth.Visitor
import my.zukoap.composablechat.domain.repository.ConditionRepository
import my.zukoap.composablechat.domain.repository.MessageRepository
import my.zukoap.composablechat.domain.use_cases.PersonUseCase
import my.zukoap.composablechat.domain.use_cases.VisitorUseCase

@ExperimentalPagingApi
class ChatRemoteMediator(
    private val conditionRepository: ConditionRepository,
    private val personUseCase: PersonUseCase,
    private val messageRepository: MessageRepository,
    private val scope: CoroutineScope,
    private val visitor: Visitor
) : RemoteMediator<Int, MessageEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MessageEntity>
    ): MediatorResult {
        return try {
            Log.d("LOADTYPE", loadType.toString())
            val loadKey = when (loadType) {
                LoadType.REFRESH ->
                    // In this example, you never need to prepend, since REFRESH
                    // will always load the first page in the list. Immediately
                    // return, reporting end of pagination.
                    return MediatorResult.Success(endOfPaginationReached = false)
                LoadType.PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = false)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    // You must explicitly check if the last item is null when
                    // appending, since passing null to networkService is only
                    // valid for initial load. If lastItem is null it means no
                    // items were loaded after the initial REFRESH and there are
                    // no more items to load.
                    lastItem.timestamp
                }
            }

            val messages =
                withContext(scope.coroutineContext + IO) {
                    messageRepository.uploadMessages(
                        uuid = visitor.uuid,
                        startTime = null,
                        endTime = loadKey,
                        updateReadPoint = { false },
                        syncMessagesAcrossDevices = {},
                        returnedEmptyPool = {
                            conditionRepository.saveFlagAllHistoryLoaded(true)
                        },
                        getPersonPreview = { personId ->
                            personUseCase.getPersonPreview(personId, visitor.token)
                        },
                        getFileInfo = messageRepository::getFileInfo
                    )
                }
            scope.launch(IO) {
                messageRepository.updatePersonNames(messages, personUseCase::updatePersonName)
                messageRepository.mergeNewMessages()
            }

            MediatorResult.Success(endOfPaginationReached = messages.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

}
