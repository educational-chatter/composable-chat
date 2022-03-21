package my.zukoap.composablechat.data.repository

import my.zukoap.composablechat.data.local.db.dao.FileDao
import my.zukoap.composablechat.data.local.db.dao.MessageDao
import my.zukoap.composablechat.data.remote.socket.SocketApi
import my.zukoap.composablechat.domain.entity.auth.Visitor
import my.zukoap.composablechat.domain.repository.AuthRepository
import my.zukoap.composablechat.presentation.ChatEventListener
import org.koin.core.component.KoinComponent
import java.io.File
import javax.inject.Inject

class AuthRepositoryImpl(
    private val socketApi: SocketApi,
    private val fileDao: FileDao ,
    private val messageDao: MessageDao
) : AuthRepository {

    override fun logIn(
        visitor: Visitor,
        successAuthUi: () -> Unit,
        failAuthUi: () -> Unit,
        successAuthUx: suspend () -> Unit,
        failAuthUx: suspend () -> Unit,
        sync: suspend () -> Unit,
        updateCurrentReadMessageTime: (newTimeMark: Long) -> Unit,
        updateCountUnreadMessages: (countNewMessages: Int, hasUserMessage: Boolean) -> Unit,
        getPersonPreview: suspend (personId: String) -> String?,
        updatePersonName: suspend (personId: String?, currentPersonName: String?) -> Unit,
        chatEventListener: ChatEventListener?
    ) {
        socketApi.setVisitor(
            visitor,
            successAuthUi,
            failAuthUi,
            successAuthUx,
            failAuthUx,
            sync,
            updateCurrentReadMessageTime,
            updateCountUnreadMessages,
            getPersonPreview,
            updatePersonName,
            chatEventListener
        )
    }

    override fun logOut(filesDir: File) {
        fileDao.getFilesNames().forEach { fileName ->
            fileDao.deleteFile(fileName)
            File(filesDir, fileName).delete()
        }
        messageDao.deleteAllMessages()
    }

}