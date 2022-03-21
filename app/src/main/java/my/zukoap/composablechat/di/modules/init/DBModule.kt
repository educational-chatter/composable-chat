package my.zukoap.composablechat.di.modules.init

import androidx.room.Room
import androidx.room.RoomDatabase
import my.zukoap.composablechat.data.local.db.dao.ChatRemoteKeysDao
import my.zukoap.composablechat.data.local.db.dao.FileDao
import my.zukoap.composablechat.data.local.db.dao.MessageDao
import my.zukoap.composablechat.data.local.db.dao.PersonDao
import my.zukoap.composablechat.data.local.db.database.ChatDatabase
import my.zukoap.composablechat.data.local.db.migrations.Migration_1_2
import my.zukoap.composablechat.data.local.db.migrations.Migration_2_3
import my.zukoap.composablechat.data.local.db.migrations.Migration_3_4
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/*@Module
class DBModule {

    @Provides
    @Singleton
    fun provideChatDatabase(
        context: Context
    ): ChatDatabase = Room.databaseBuilder(
        context,
        ChatDatabase::class.java,
        "chat.db"
    ).setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
        .addMigrations(Migration_1_2, Migration_2_3, Migration_3_4)
        .build()

    @Provides
    @Singleton
    fun provideMessagesDao(
        chatDatabase: ChatDatabase
    ): MessageDao = chatDatabase.messageDao()

    @Provides
    @Singleton
    fun providePersonDao(
        chatDatabase: ChatDatabase
    ): PersonDao = chatDatabase.personDao()

    @Provides
    @Singleton
    fun provideFileDao(
        chatDatabase: ChatDatabase
    ): FileDao = chatDatabase.fileDao()

    @Provides
    @Singleton
    fun provideChatRemoteKeysDao(
        chatDatabase: ChatDatabase
    ): ChatRemoteKeysDao = chatDatabase.chatRemoteKeysDao()

}*/

val databaseModule = module {
    single<ChatDatabase> {
        Room.databaseBuilder(
            androidContext(),
            ChatDatabase::class.java,
            "chat.db"
        ).setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .addMigrations(Migration_1_2, Migration_2_3, Migration_3_4)
            .build()
    }

    single<MessageDao> {
        get<ChatDatabase>().messageDao()
    }
    single<PersonDao> {
        get<ChatDatabase>().personDao()
    }
    single<FileDao> {
        get<ChatDatabase>().fileDao()
    }
    single<ChatRemoteKeysDao> {
        get<ChatDatabase>().chatRemoteKeysDao()
    }
}