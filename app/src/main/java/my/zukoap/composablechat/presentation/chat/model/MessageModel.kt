package my.zukoap.composablechat.presentation.chat.model

import androidx.compose.ui.text.AnnotatedString
import my.zukoap.composablechat.presentation.base.BaseItem
import my.zukoap.composablechat.presentation.chat.model.Role.NEUTRAL
import my.zukoap.composablechat.presentation.chat.model.Role.OPERATOR
import my.zukoap.composablechat.domain.entity.message.MessageType as StateMessage

sealed class MessageModel(
    open val id: String,
    open val role: Role,
    open val timestamp: Long,
    open val authorName: String,
    open val authorPreview: String? = null,
    open val stateCheck: StateMessage,
    //var isLastMessageInDay: Boolean = false
) : BaseItem() {
    override fun <T : BaseItem> isSame(item: T): Boolean {
        return item is MessageModel && item.id == id
    }
}

data class SeparateItem(
    override val timestamp: Long
) : MessageModel("", NEUTRAL, timestamp, "", null, StateMessage.INFO_MESSAGE)

data class DefaultMessageItem(
    override val id: String,
    override val timestamp: Long
) : MessageModel(id, NEUTRAL, timestamp, "", null, StateMessage.DEFAULT)//, false)


data class TextMessageItem(
    override val id: String,
    override val role: Role,
    val message: AnnotatedString,
    val actions: List<ActionItem>?,
    val hasSelectedAction: Boolean,
    val repliedMessage: RepliedMessageModel?,
    override val timestamp: Long,
    override val authorName: String,
    override val authorPreview: String?,
    override val stateCheck: StateMessage
) : MessageModel(id, role, timestamp, authorName, authorPreview, stateCheck)

data class ImageMessageItem(
    override val id: String,
    override val role: Role,
    val image: FileModel,
    override val timestamp: Long,
    override val authorName: String,
    override val authorPreview: String?,
    override val stateCheck: StateMessage
) : MessageModel(id, role, timestamp, authorName, authorPreview, stateCheck)

data class GifMessageItem(
    override val id: String,
    override val role: Role,
    val gif: FileModel,
    override val timestamp: Long,
    override val authorName: String,
    override val authorPreview: String?,
    override val stateCheck: StateMessage
) : MessageModel(id, role, timestamp, authorName, authorPreview, stateCheck)

data class FileMessageItem(
    override val id: String,
    override val role: Role,
    val document: FileModel,
    override val timestamp: Long,
    override val authorName: String,
    override val authorPreview: String?,
    override val stateCheck: StateMessage
) : MessageModel(id, role, timestamp, authorName, authorPreview, stateCheck)

data class UnionMessageItem(
    override val id: String,
    override val role: Role,
    val message: AnnotatedString,
    val actions: List<ActionItem>?,
    val hasSelectedAction: Boolean,
    val file: FileModel,
    override val timestamp: Long,
    override val authorName: String,
    override val authorPreview: String?,
    override val stateCheck: StateMessage
) : MessageModel(id, role, timestamp, authorName, authorPreview, stateCheck)

data class TransferMessageItem(
    override val id: String,
    override val timestamp: Long,
    override val authorName: String,
    override val authorPreview: String?
) : MessageModel(
    id,
    //NEUTRAL,
    OPERATOR,
    timestamp,
    authorName,
    authorPreview,
    StateMessage.TRANSFER_TO_OPERATOR
)

data class InfoMessageItem(
    override val id: String,
    val message: AnnotatedString,
    override val timestamp: Long
) : MessageModel(id, NEUTRAL, timestamp, "", null, StateMessage.INFO_MESSAGE)