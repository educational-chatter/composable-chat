package my.zukoap.composablechat.data.local.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import my.zukoap.composablechat.data.local.db.entity.ChatRemoteKeysEntity
import my.zukoap.composablechat.data.local.db.entity.MessageEntity

@Dao
interface ChatRemoteKeysDao {

    @Query("SELECT * FROM ${ChatRemoteKeysEntity.CHAT_REMOTE_KEYS_TABLE} WHERE id=:id")
    suspend fun getRemoteKeys(id: String): ChatRemoteKeysEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllRemoteKeys(remoteKeys: List<ChatRemoteKeysEntity>)

    @Query("DELETE FROM ${ChatRemoteKeysEntity.CHAT_REMOTE_KEYS_TABLE}")
    suspend fun deleteAllRemoteKeys()

}