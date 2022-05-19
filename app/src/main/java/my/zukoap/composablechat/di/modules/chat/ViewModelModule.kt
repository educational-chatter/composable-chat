package my.zukoap.composablechat.di.modules.chat

import my.zukoap.composablechat.domain.use_cases.*
import my.zukoap.composablechat.presentation.chat.ComposableChatViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/*@Module
object ViewModelModule {

    @Provides
    @ChatScope
    fun provideChatViewModelFactory(
        authChatUseCase: AuthUseCase,
        messageUseCase: MessageUseCase,
        fileUseCase: FileUseCase,
        conditionUseCase: ConditionUseCase,
        feedbackUseCase: FeedbackUseCase,
        configurationUseCase: ConfigurationUseCase,
        context: Context
    ): ComposableChatViewModelFactory = ComposableChatViewModelFactory(
        authChatUseCase,
        messageUseCase,
        fileUseCase,
        conditionUseCase,
        feedbackUseCase,
        configurationUseCase,
        context
    )


*//*    @Provides
    @ChatScope
    fun provideChatViewModel(
        parentFragment: Fragment,
        chatViewModelFactory: ComposableChatViewModelFactory
    ): ComposableChatViewModel = ViewModelProvider(parentFragment, chatViewModelFactory).get(ComposableChatViewModel::class.java)*//*

}*/

val viewModelModule = module {

/*        factory<ComposableChatViewModelFactory> {
            ComposableChatViewModelFactory(
                get<AuthUseCase>(),
                get<MessageUseCase>(),
                get<FileUseCase>(),
                get<ConditionUseCase>(),
                get<FeedbackUseCase>(),
                get<ConfigurationUseCase>(),
                androidContext()
            )
        }*/

        viewModel<ComposableChatViewModel> {
            ComposableChatViewModel(
                get<AuthUseCase>(),
                get<MessageUseCase>(),
                get<FileUseCase>(),
                get<ConditionUseCase>(),
                get<FeedbackUseCase>(),
                get<ConfigurationUseCase>(),
                androidContext()
            )
        }

}
