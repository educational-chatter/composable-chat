package my.zukoap.composablechat.presentation.chat.model

import android.content.Context
import android.text.SpannableString
import my.zukoap.composablechat.data.local.db.entity.MessageEntity
import my.zukoap.composablechat.domain.entity.file.TypeDownloadProgress
import my.zukoap.composablechat.domain.entity.file.TypeFile

data class RepliedMessageModel(
    val id: String,
    val textMessage: String? = null,
    val file: FileModel? = null
) {

    companion object {

        fun map(localMessage: MessageEntity): RepliedMessageModel? {
            val repliedMessageId = localMessage.repliedMessageId ?: return null
            val repliedMessageText = localMessage.repliedMessageText//.convertToSpannableString(!localMessage.isReply, localMessage.repliedTextSpanStructureList, context)

            val isNotAttachmentFile = localMessage.repliedMessageAttachmentUrl == null ||
                    localMessage.repliedMessageAttachmentType == null ||
                    localMessage.repliedMessageAttachmentName == null

            if (repliedMessageText == null && isNotAttachmentFile) {
                return null
            } else {
                val file = if (isNotAttachmentFile) {
                    null
                } else {
                    FileModel(
                        url = localMessage.repliedMessageAttachmentUrl!!,
                        name = localMessage.repliedMessageAttachmentName!!,
                        size = localMessage.repliedMessageAttachmentSize,
                        height = localMessage.repliedMessageAttachmentHeight,
                        width = localMessage.repliedMessageAttachmentWidth,
                        failLoading = (localMessage.attachmentType in listOf( TypeFile.IMAGE, TypeFile.GIF)) && (localMessage.height == null || localMessage.width == null),
                        type = localMessage.repliedMessageAttachmentType,
                        typeDownloadProgress = localMessage.repliedMessageAttachmentDownloadProgressType ?: TypeDownloadProgress.NOT_DOWNLOADED
                    )
                }

                return RepliedMessageModel(
                    id = repliedMessageId,
                    textMessage = repliedMessageText,
                    file = file
                )
            }
        }

    }

}