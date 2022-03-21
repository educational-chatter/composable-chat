package my.zukoap.composablechat.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import my.zukoap.composablechat.data.local.db.entity.FileEntity
import androidx.room.Query

@Dao
interface FileDao {

    @Query("SELECT file_name FROM files")
    fun getFilesNames(): List<String>

    @Insert
    fun addFile(file: FileEntity)

    @Query("DELETE FROM files WHERE file_name = :fileName")
    fun deleteFile(fileName: String)

}