package my.zukoap.composablechat.domain.entity.message

import com.google.gson.annotations.SerializedName
import my.zukoap.composablechat.common.ChatParams
import my.zukoap.composablechat.data.local.db.entity.MessageEntity
import my.zukoap.composablechat.domain.entity.file.TypeFile
import java.io.Serializable

data class NetworkMessage (

    @SerializedName (value = "id")
    var id: String?,

    @SerializedName (value = "id_from_channel")
    val idFromChannel: String? = null,

    @SerializedName (value = "message_type")
    val messageType: Int,

    @SerializedName (value = "isReply")
    val isReply : Boolean,

    @SerializedName (value = "parent_message_id", alternate = ["parent_msg_id"])
    val parentMessageId: String? = null,

    @SerializedName (value = "timestamp")
    val timestamp: Long,

    @SerializedName (value = "message")
    var message: String? = null,

    @SerializedName (value = "action")
    val selectedAction: String? = null,

    @SerializedName (value = "actions")
    val actions: List<NetworkAction>? = null,

    @SerializedName (value = "attachment_url")
    var attachmentUrl: String? = null,

    @SerializedName (value = "attachment_type")
    val attachmentType: String? = null,

    @SerializedName (value = "attachment_name")
    val attachmentName: String? = null,

    @SerializedName (value = "operator_id")
    val operatorId: String? = null,

    @SerializedName (value = "operator_name")
    val operatorName: String? = null,

    @SerializedName (value = "reply_to_message")
    val replyToMessage: NetworkMessage? = null,

    @SerializedName (value = "dialog_id")
    val dialogId: String? = null

) : Serializable {

    val isText: Boolean
    get() = !message.isNullOrBlank()

    val isImage: Boolean
    get() = !attachmentUrl.isNullOrEmpty() &&
            !attachmentName.isNullOrEmpty() &&
            !attachmentType.isNullOrEmpty() &&
            (attachmentType == "IMAGE" || attachmentType.lowercase(
                ChatParams.locale!!
            ).startsWith("image"))

    val isGif: Boolean
    get() = !attachmentUrl.isNullOrEmpty() &&
            !attachmentName.isNullOrEmpty() &&
            !attachmentType.isNullOrEmpty() &&
            (attachmentType == "IMAGE" || attachmentType.lowercase(
                ChatParams.locale!!
            ).startsWith("image")) &&
            attachmentName.contains(".GIF", true)

    val isFile: Boolean
    get() = !attachmentUrl.isNullOrEmpty() &&
            !attachmentName.isNullOrEmpty() &&
            !attachmentType.isNullOrEmpty() &&
            attachmentType == "FILE"

    val isContainsContent: Boolean
    get() = isText || isImage || isGif || isFile

    val attachmentTypeFile: TypeFile?
    get() = when {
        isFile -> TypeFile.FILE
        isImage -> TypeFile.IMAGE
        isGif -> TypeFile.GIF
        else -> null
    }

    companion object {

        fun map(messageEntity: MessageEntity) = NetworkMessage(
            id = messageEntity.id,
            messageType = messageEntity.messageType,
            isReply = messageEntity.isReply,
            parentMessageId = messageEntity.parentMsgId,
            timestamp = messageEntity.timestamp,
            message = messageEntity.message,
            actions = messageEntity.actions?.map { NetworkAction.map(it) },
            attachmentUrl = messageEntity.attachmentUrl,
            attachmentType = messageEntity.attachmentType?.name,
            attachmentName = messageEntity.attachmentName,
            operatorId = messageEntity.operatorId,
            operatorName = messageEntity.operatorName,
            replyToMessage = null,
            dialogId = messageEntity.dialogId
        )

    }

}