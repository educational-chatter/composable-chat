package my.zukoap.composablechat.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import my.zukoap.composablechat.data.local.db.entity.ChatRemoteKeysEntity.Companion.CHAT_REMOTE_KEYS_TABLE


@Entity(tableName = CHAT_REMOTE_KEYS_TABLE)
data class ChatRemoteKeysEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val prevPage: Int?,
    val nextPage: Int?
) {
    companion object {
        const val CHAT_REMOTE_KEYS_TABLE = "chat_remote_keys_table"
    }
}