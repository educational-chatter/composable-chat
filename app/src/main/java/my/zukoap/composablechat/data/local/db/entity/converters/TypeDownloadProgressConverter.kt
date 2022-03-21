package my.zukoap.composablechat.data.local.db.entity.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import my.zukoap.composablechat.domain.entity.file.TypeDownloadProgress
import java.lang.reflect.Type

class TypeDownloadProgressConverter {

    @TypeConverter
    fun fromTypeFile(typeDownloadProgress: TypeDownloadProgress?): String? {
        typeDownloadProgress ?: return null
        val type: Type = object : TypeToken<TypeDownloadProgress>() {}.type
        val gson = Gson()
        return gson.toJson(typeDownloadProgress, type)
    }

    @TypeConverter
    fun toTypeFile(typeDownloadProgress: String?): TypeDownloadProgress? {
        typeDownloadProgress ?: return null
        val type: Type = object : TypeToken<TypeDownloadProgress>() {}.type
        val gson = Gson()
        return gson.fromJson(typeDownloadProgress, type)
    }

}