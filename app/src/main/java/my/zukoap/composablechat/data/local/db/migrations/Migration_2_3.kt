package my.zukoap.composablechat.data.local.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import my.zukoap.composablechat.data.local.db.entity.MessageEntity

object Migration_2_3: Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {

        database.execSQL("ALTER TABLE ${MessageEntity.TABLE_NAME} ADD COLUMN attachment_download_progress_type TEXT DEFAULT NULL")

    }
}