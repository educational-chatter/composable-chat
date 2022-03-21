package my.zukoap.composablechat.data.paging

/*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import my.zukoap.composablechat.data.local.db.dao.MessageDao
import my.zukoap.composablechat.data.local.db.database.ChatDatabase
import my.zukoap.composablechat.data.local.db.entity.ChatRemoteKeysEntity
import my.zukoap.composablechat.data.local.db.entity.MessageEntity
import my.zukoap.composablechat.data.remote.rest.MessageApi
import retrofit2.await
import javax.inject.Inject

@ExperimentalPagingApi
class ChatRemoteMediator @Inject constructor(
    private val messageDao: MessageDao,
    private val chatDatabase: ChatDatabase
) : RemoteMediator<Int, MessageEntity>() {

    private val chatRemoteKeysDao = chatDatabase.chatRemoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MessageEntity>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextPage?.minus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevPage = remoteKeys?.prevPage
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    prevPage
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextPage = remoteKeys?.nextPage
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    nextPage
                }
            }

            val response = messageDao.getMessages()
            val endOfPaginationReached = response.isEmpty()

            val prevPage = if (currentPage == 1) null else currentPage - 1
            val nextPage = if (endOfPaginationReached) null else currentPage + 1


            chatDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    messageDao.deleteAllMessages()
                    chatRemoteKeysDao.deleteAllRemoteKeys()
                }
                val keys = response.map { message ->
                    ChatRemoteKeysEntity(
                        id = message.id!!,
                        prevPage = prevPage,
                        nextPage = nextPage
                    )
                }
                chatRemoteKeysDao.addAllRemoteKeys(remoteKeys = keys)
                messageDao.addImages(images = response)
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, MessageEntity>
    ): ChatRemoteKeysEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                chatRemoteKeysDao.getRemoteKeys(id = id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, MessageEntity>
    ): ChatRemoteKeysEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { chatImage ->
                chatRemoteKeysDao.getRemoteKeys(id = chatImage.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, MessageEntity>
    ): ChatRemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { chatImage ->
                chatRemoteKeysDao.getRemoteKeys(id = chatImage.id)
            }
    }

}*/
