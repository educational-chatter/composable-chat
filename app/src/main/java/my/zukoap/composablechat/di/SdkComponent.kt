package my.zukoap.composablechat.di

import my.zukoap.composablechat.di.modules.chat.chatNetworkModule
import my.zukoap.composablechat.di.modules.chat.chatRepositoryModule
import my.zukoap.composablechat.di.modules.chat.viewModelModule
import my.zukoap.composablechat.di.modules.init.*
import my.zukoap.composablechat.domain.repository.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/*@Singleton
@Component(
    modules = [
        NetworkModule::class,
        RepositoryModule::class,
        SharedPreferencesModule::class,
        GsonModule::class,
        DBModule::class
    ]
)
interface SdkComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder
        fun build(): SdkComponent
    }

    fun getConditionRepository(): ConditionRepository
    fun getAuthRepository(): AuthRepository
    fun getVisitorRepository(): VisitorRepository
    fun getPersonRepository(): PersonRepository
    fun getNotificationRepository(): NotificationRepository


    // fun createChatComponent(): ChatComponent.Builder
}*/


class KoinSdkComponent : KoinComponent {
    val conditionRepository: ConditionRepository by inject()
    val authRepository: AuthRepository by inject()
    val visitorRepository: VisitorRepository by inject()
    val personRepository: PersonRepository by inject()
    val notificationRepository: NotificationRepository by inject()
}

val sdkModules =
    listOf(networkModule, repositoryModule, sharedPreferencesModule, gsonModule, databaseModule,
        chatRepositoryModule, chatNetworkModule, viewModelModule, useCaseModule)