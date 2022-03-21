package my.zukoap.composablechat.data.local.db.entity.converters

import androidx.room.TypeConverter
import my.zukoap.composablechat.domain.entity.message.MessageType
import my.zukoap.composablechat.domain.entity.message.MessageType.Companion.getMessageTypeByValueType

class MessageTypeConverter {

    @TypeConverter
    fun fromMessageType(messageType: MessageType): Int {
        return messageType.valueType
    }

    @TypeConverter
    fun toMessageType(messageType: Int): MessageType {
        return getMessageTypeByValueType(messageType)
    }

}