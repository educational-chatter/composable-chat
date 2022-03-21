package my.zukoap.composablechat.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files")
data class FileEntity(
    val uuid: String,
    @ColumnInfo(name = "file_name")
    val fileName: String
) {
    @PrimaryKey(autoGenerate = true)
    var idKey: Long = 0
}