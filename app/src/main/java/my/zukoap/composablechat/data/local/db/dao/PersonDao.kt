package my.zukoap.composablechat.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import my.zukoap.composablechat.data.local.db.entity.PersonEntity

@Dao
interface PersonDao {

    @Query("SELECT person_preview FROM persons WHERE person_id = :personId")
    fun getPersonPreview(personId: String): String?

    @Insert
    fun addPersonPreview(person: PersonEntity)

}