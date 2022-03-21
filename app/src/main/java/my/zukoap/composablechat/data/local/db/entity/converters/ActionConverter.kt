package my.zukoap.composablechat.data.local.db.entity.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import my.zukoap.composablechat.data.local.db.entity.ActionEntity
import java.lang.reflect.Type

class ActionConverter {

    @TypeConverter
    fun fromActions(actions: List<ActionEntity>?): String? {
        actions ?: return null
        val type: Type = object : TypeToken<List<ActionEntity>>() {}.type
        val gson = Gson()
        return gson.toJson(actions, type)
    }

    @TypeConverter
    fun toActions(actions: String?): List<ActionEntity>? {
        actions ?: return null
        val type: Type = object : TypeToken<List<ActionEntity>>() {}.type
        val gson = Gson()
        return gson.fromJson(actions, type)
    }

}