package my.zukoap.composablechat.data.local.db.entity.deserializers

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import my.zukoap.composablechat.domain.entity.tags.*
import java.lang.reflect.Type

class TagDeserializer(val gson: Gson): JsonDeserializer<List<Tag>> {
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): List<Tag> {
        val result = arrayListOf<Tag>()
        val jsonArray = json.asJsonArray
        jsonArray.forEach {
            val jsonObject = it.asJsonObject
            val name: String = jsonObject.get("name").asString
            val pointStart: Int = jsonObject.get("pointStart").asInt
            val pointEnd: Int = jsonObject.get("pointEnd").asInt
            when (name) {
                "strike" -> result.add(StrikeTag(pointStart, pointEnd))
                "strong" -> result.add(StrongTag(pointStart, pointEnd))
                "b" -> result.add(BTag(pointStart, pointEnd))
                "i" -> result.add(ItalicTag(pointStart, pointEnd))
                "em" -> result.add(EmTag(pointStart, pointEnd))
                "a" -> result.add(UrlTag(pointStart, pointEnd, jsonObject.get("url").asString))
                "img" -> result.add(ImageTag(pointStart, pointEnd, jsonObject.get("url").asString, jsonObject.get("width").asInt, jsonObject.get("height").asInt))
                "li" -> result.add(ItemListTag(pointStart, pointEnd, jsonObject.get("countNesting").asInt))
                "ul" -> result.add(HostListTag(pointStart, pointEnd, jsonObject.get("countNesting").asInt))
                "phone" -> result.add(PhoneTag(pointStart, pointEnd, jsonObject.get("phone").asString))
            }
        }
        return result
    }
}