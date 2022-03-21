package my.zukoap.composablechat.domain.repository

import my.zukoap.composablechat.domain.entity.auth.Visitor
import my.zukoap.composablechat.presentation.ChatEventListener
import java.io.File
import javax.inject.Singleton

interface AuthRepository {
    fun logIn(
        visitor: Visitor,
        successAuthUi: () -> Unit,
        failAuthUi: () -> Unit,
        successAuthUx: suspend () -> Unit,
        failAuthUx: suspend () -> Unit,
        sync: suspend () -> Unit,
        updateCurrentReadMessageTime: (newTimeMark: Long) -> Unit,
        updateCountUnreadMessages: (Int, Boolean) -> Unit,
        getPersonPreview: suspend (personId: String) -> String?,
        updatePersonName: suspend (personId: String?, currentPersonName: String?) -> Unit,
        chatEventListener: ChatEventListener?
    )
    fun logOut(filesDir: File)
}