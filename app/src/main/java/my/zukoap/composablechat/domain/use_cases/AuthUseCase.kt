package my.zukoap.composablechat.domain.use_cases

import android.util.Log
import my.zukoap.composablechat.common.AuthType
import my.zukoap.composablechat.common.ChatParams
import my.zukoap.composablechat.common.ChatStatus
import my.zukoap.composablechat.domain.entity.auth.Visitor
import my.zukoap.composablechat.domain.repository.AuthRepository
import my.zukoap.composablechat.presentation.ChatEventListener
import org.koin.core.component.KoinComponent
import java.io.File

class AuthUseCase(
    private val authRepository: AuthRepository,
    private val visitorUseCase: VisitorUseCase,
    private val conditionUseCase: ConditionUseCase,
    private val personUseCase: PersonUseCase,
    private val notificationUseCase: NotificationUseCase
){

    private fun dataPreparation(visitor: Visitor?): Visitor? {
        return when (ChatParams.authMode) {
            AuthType.AUTH_WITH_FORM -> visitor?.apply(visitorUseCase::saveVisitor)
            AuthType.AUTH_WITHOUT_FORM -> visitor?.apply(visitorUseCase::setVisitor)
            else -> null
        } ?: visitorUseCase.getVisitor()
    }

    fun logIn(
        visitor: Visitor? = null,
        successAuthUi: () -> Unit = {},
        failAuthUi: () -> Unit = {},
        successAuthUx: suspend () -> Unit = {},
        failAuthUx: suspend () -> Unit = {},
        sync: suspend () -> Unit = {},
        updateCurrentReadMessageTime: (Long) -> Boolean = { false },
        firstLogInWithForm: () -> Unit = {},
        chatEventListener: ChatEventListener? = null
    ) {
        val currentVisitor = dataPreparation(visitor)

        val successAuthUiWrapper = {
            if (conditionUseCase.getStatusChat() == ChatStatus.ON_CHAT_SCREEN_FOREGROUND_APP) {
                successAuthUi()
            }
        }

        val failAuthUiWrapper = {
            if (conditionUseCase.getStatusChat() == ChatStatus.ON_CHAT_SCREEN_FOREGROUND_APP) {
                failAuthUi()
            }
        }

        val successAuthUxWrapper = suspend {
            successAuthUx()
            notificationUseCase.subscribeNotification()
        }

        val failAuthUxWrapper = suspend {
            failAuthUx()
            notificationUseCase.unsubscribeNotification()
        }

        val syncWrapper = suspend {
            if (conditionUseCase.getStatusChat() == ChatStatus.ON_CHAT_SCREEN_FOREGROUND_APP) {
                sync()
            }
        }

        val updateCurrentReadMessageTimeWrapper: (newTimeMark: Long) -> Unit = { newTimeMark ->
            when (conditionUseCase.getStatusChat()) {
                ChatStatus.ON_CHAT_SCREEN_FOREGROUND_APP -> updateCurrentReadMessageTime(newTimeMark)
                ChatStatus.NOT_ON_CHAT_SCREEN_FOREGROUND_APP -> {
                    val currentReadMessageTime = conditionUseCase.getCurrentReadMessageTime()
                    if (newTimeMark > currentReadMessageTime) {
                        conditionUseCase.saveCurrentReadMessageTime(newTimeMark)
                    }
                }
            }
        }

        val updateCountUnreadMessagesWrapper: (countNewMessages: Int, hasUserMessage: Boolean) -> Unit =
            { countNewUnreadMessages, hasUserMessage ->
                if (conditionUseCase.getStatusChat() == ChatStatus.NOT_ON_CHAT_SCREEN_FOREGROUND_APP) {
                    if (hasUserMessage) {
                        conditionUseCase.saveCountUnreadMessages(countNewUnreadMessages)
                    } else {
                        val oldCountUnreadMessages = conditionUseCase.getCountUnreadMessages()
                        conditionUseCase.saveCountUnreadMessages(oldCountUnreadMessages + countNewUnreadMessages)
                    }
                }
            }

        val getPersonPreviewWrapper: suspend (personId: String) -> String? = { personId ->
            currentVisitor?.token?.let { token ->
                personUseCase.getPersonPreview(personId, token)
            }
        }

        when (ChatParams.authMode) {
            AuthType.AUTH_WITH_FORM -> {
                if (currentVisitor == null) {
                    firstLogInWithForm()
                } else {
                    authRepository.logIn(
                        visitor = currentVisitor,
                        successAuthUi = successAuthUiWrapper,
                        failAuthUi = failAuthUiWrapper,
                        successAuthUx = successAuthUxWrapper,
                        failAuthUx = failAuthUxWrapper,
                        sync = syncWrapper,
                        updateCurrentReadMessageTime = updateCurrentReadMessageTimeWrapper,
                        updateCountUnreadMessages = updateCountUnreadMessagesWrapper,
                        getPersonPreview = getPersonPreviewWrapper,
                        updatePersonName = personUseCase::updatePersonName,
                        chatEventListener = chatEventListener
                    )
                }
            }
            AuthType.AUTH_WITHOUT_FORM -> {
                authRepository.logIn(
                    visitor = currentVisitor!!,
                    successAuthUi = successAuthUiWrapper,
                    failAuthUi = failAuthUiWrapper,
                    successAuthUx = successAuthUxWrapper,
                    failAuthUx = failAuthUxWrapper,
                    sync = syncWrapper,
                    updateCurrentReadMessageTime = updateCurrentReadMessageTimeWrapper,
                    updateCountUnreadMessages = updateCountUnreadMessagesWrapper,
                    getPersonPreview = getPersonPreviewWrapper,
                    updatePersonName = personUseCase::updatePersonName,
                    chatEventListener = chatEventListener
                )
            }
        }
    }

    fun logOut(filesDir: File) {
        try {
            notificationUseCase.unsubscribeNotification()
            authRepository.logOut(filesDir)
        } catch (ex: Exception) {
            Log.e("FAIL logOut", "${ex.message}")
        }
        conditionUseCase.clearDataChatState()
        visitorUseCase.clearDataVisitor()
    }

}