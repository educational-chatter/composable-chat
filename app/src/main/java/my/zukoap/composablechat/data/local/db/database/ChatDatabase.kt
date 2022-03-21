package my.zukoap.composablechat.data.local.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import my.zukoap.composablechat.data.local.db.dao.ChatRemoteKeysDao
import my.zukoap.composablechat.data.local.db.dao.FileDao
import my.zukoap.composablechat.data.local.db.dao.MessageDao
import my.zukoap.composablechat.data.local.db.dao.PersonDao
import my.zukoap.composablechat.data.local.db.entity.ChatRemoteKeysEntity
import my.zukoap.composablechat.data.local.db.entity.FileEntity
import my.zukoap.composablechat.data.local.db.entity.MessageEntity
import my.zukoap.composablechat.data.local.db.entity.PersonEntity
import my.zukoap.composablechat.data.local.db.entity.converters.ActionConverter
import my.zukoap.composablechat.data.local.db.entity.converters.SpanStructureListConverter
import my.zukoap.composablechat.data.local.db.entity.converters.TypeDownloadProgressConverter
import my.zukoap.composablechat.data.local.db.entity.converters.TypeFileConverter

@Database(
    entities = [MessageEntity::class, PersonEntity::class, FileEntity::class, ChatRemoteKeysEntity::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(
    ActionConverter::class,
    TypeFileConverter::class,
    TypeDownloadProgressConverter::class,
    SpanStructureListConverter::class
)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun personDao(): PersonDao
    abstract fun fileDao(): FileDao
    abstract fun chatRemoteKeysDao(): ChatRemoteKeysDao
}