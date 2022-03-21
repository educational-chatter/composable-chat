package my.zukoap.composablechat.di.modules.chat

import my.zukoap.composablechat.data.helper.file.FileInfoHelper
import my.zukoap.composablechat.data.helper.file.RequestHelper
import my.zukoap.composablechat.data.local.db.dao.FileDao
import my.zukoap.composablechat.data.local.db.dao.MessageDao
import my.zukoap.composablechat.data.remote.rest.ConfigurationApi
import my.zukoap.composablechat.data.remote.rest.FileApi
import my.zukoap.composablechat.data.remote.socket.SocketApi
import my.zukoap.composablechat.data.repository.ConfigurationRepositoryImpl
import my.zukoap.composablechat.data.repository.FeedbackRepositoryImpl
import my.zukoap.composablechat.data.repository.FileRepositoryImpl
import my.zukoap.composablechat.data.repository.MessageRepositoryImpl
import my.zukoap.composablechat.domain.repository.ConfigurationRepository
import my.zukoap.composablechat.domain.repository.FeedbackRepository
import my.zukoap.composablechat.domain.repository.FileRepository
import my.zukoap.composablechat.domain.repository.MessageRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

/*
@Module
abstract class RepositoryModule {

    @ChatScope
    @Binds
    abstract fun bindFileRepository(fileRepositoryImpl: FileRepositoryImpl): FileRepository

    @ChatScope
    @Binds
    abstract fun bindMessageRepository(messageRepositoryImpl: MessageRepositoryImpl): MessageRepository

    @ChatScope
    @Binds
    abstract fun bindFeedbackRepository(feedbackRepositoryImpl: FeedbackRepositoryImpl): FeedbackRepository

    @ChatScope
    @Binds
    abstract fun bindConfigurationRepository(configurationRepositoryImpl: ConfigurationRepositoryImpl): ConfigurationRepository

}*/

val chatRepositoryModule = module {

    factory<FileInfoHelper> {
        FileInfoHelper(androidContext())
    }

    factory<RequestHelper> {
        RequestHelper(androidContext())
    }

    //scope(named("chatScope")) {
        factory<FileRepository> {
            FileRepositoryImpl(
                get<FileApi>(),
                get<FileDao>(),
                get<FileInfoHelper>(),
                get<RequestHelper>(),
            ) as FileRepository
        }

        factory<MessageRepository> {
            MessageRepositoryImpl(
                androidContext(),
                get<MessageDao>(),
                get<SocketApi>(),
            ) as MessageRepository
        }

        factory<FeedbackRepository> {
            FeedbackRepositoryImpl(
                get<SocketApi>()
            ) as FeedbackRepository
        }

        factory<ConfigurationRepository> {
            ConfigurationRepositoryImpl(
                get<ConfigurationApi>()
            ) as ConfigurationRepository
        }
   // }
}