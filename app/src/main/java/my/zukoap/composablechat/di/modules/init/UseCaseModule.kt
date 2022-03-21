package my.zukoap.composablechat.di.modules.init

import my.zukoap.composablechat.domain.repository.*
import my.zukoap.composablechat.domain.use_cases.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

val useCaseModule = module {

    factory<AuthUseCase> {
        AuthUseCase(
            get<AuthRepository>(),
            get<VisitorUseCase>(),
            get<ConditionUseCase>(),
            get<PersonUseCase>(),
            get<NotificationUseCase>()
        )
    }

    factory<ConditionUseCase> {
        ConditionUseCase(get<ConditionRepository>())
    }

    factory<ConfigurationUseCase> {
        ConfigurationUseCase(get<ConfigurationRepository>())
    }

    factory<FeedbackUseCase> {
        FeedbackUseCase(get<FeedbackRepository>())
    }

    factory<FileUseCase> {
        FileUseCase(
            get<FileRepository>(),
            get<MessageRepository>(),
            get<VisitorUseCase>(),
        )
    }

    factory<MessageUseCase> {
        MessageUseCase(
            get<MessageRepository>(),
            get<ConditionRepository>(),
            get<VisitorUseCase>(),
            get<PersonUseCase>()
        )
    }

    factory<NotificationUseCase> {
        NotificationUseCase(
            get<NotificationRepository>(),
            get<VisitorUseCase>()
        )
    }

    factory<PersonUseCase> {
        PersonUseCase(get<PersonRepository>())
    }

    factory<VisitorUseCase> {
        VisitorUseCase(get<VisitorRepository>())
    }
}