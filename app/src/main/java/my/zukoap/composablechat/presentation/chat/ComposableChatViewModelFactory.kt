package my.zukoap.composablechat.presentation.chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import my.zukoap.composablechat.domain.use_cases.*

@Suppress("UNCHECKED_CAST")
class ComposableChatViewModelFactory constructor(
    private val authChatUseCase: AuthUseCase,
    private val messageUseCase: MessageUseCase,
    private val fileUseCase: FileUseCase,
    private val conditionUseCase: ConditionUseCase,
    private val feedbackUseCase: FeedbackUseCase,
    private val configurationUseCase: ConfigurationUseCase,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ComposableChatViewModel(
            authChatUseCase,
            messageUseCase,
            fileUseCase,
            conditionUseCase,
            feedbackUseCase,
            configurationUseCase,
            context
        ) as T
    }
}