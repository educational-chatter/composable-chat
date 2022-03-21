package my.zukoap.composablechat.di.modules.init

import android.content.SharedPreferences
import com.google.gson.Gson
import my.zukoap.composablechat.data.local.db.dao.FileDao
import my.zukoap.composablechat.data.local.db.dao.MessageDao
import my.zukoap.composablechat.data.local.db.dao.PersonDao
import my.zukoap.composablechat.data.remote.rest.NotificationApi
import my.zukoap.composablechat.data.remote.rest.PersonApi
import my.zukoap.composablechat.data.remote.socket.SocketApi
import my.zukoap.composablechat.data.repository.*
import my.zukoap.composablechat.domain.repository.*
import org.koin.dsl.module

/*@Module
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindConditionRepository(conditionRepositoryImpl: ConditionRepositoryImpl): ConditionRepository

    @Singleton
    @Binds
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    abstract fun bindVisitorRepository(visitorRepositoryImpl: VisitorRepositoryImpl): VisitorRepository

    @Singleton
    @Binds
    abstract fun bindNotificationRepository(notificationRepositoryImpl: NotificationRepositoryImpl): NotificationRepository

    @Singleton
    @Binds
    abstract fun bindPersonRepository(personRepositoryImpl: PersonRepositoryImpl): PersonRepository

}*/

val repositoryModule = module {
    single<ConditionRepository> {
        ConditionRepositoryImpl(
            get<MessageDao>(),
            get<SharedPreferences>(),
            get<SocketApi>()
        ) as ConditionRepository
    }// bind ConditionRepository::class

    single<AuthRepository> {
        AuthRepositoryImpl(
            get<SocketApi>(),
            get<FileDao>(),
            get<MessageDao>()
        ) as AuthRepository
    }

    single<VisitorRepository> {
        VisitorRepositoryImpl(
            get<SharedPreferences>(),
            get<Gson>()
        ) as VisitorRepository
    }

    single<NotificationRepository> {
        NotificationRepositoryImpl(
            get<NotificationApi>()
        ) as NotificationRepository
    }
    single<PersonRepository> {
        PersonRepositoryImpl(
            get<PersonDao>(),
            get<MessageDao>(),
            get<PersonApi>()
        ) as PersonRepository
    }
}