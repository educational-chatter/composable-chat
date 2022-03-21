package my.zukoap.composablechat.presentation.chat.model

import my.zukoap.composablechat.domain.entity.file.TypeDownloadProgress
import my.zukoap.composablechat.domain.entity.file.TypeFile

data class FileModel(
    val url: String,
    val name: String,
    val size: Long? = null,
    val height: Int? = null,
    val width: Int? = null,
    val failLoading: Boolean = false,
    val type: TypeFile? = null,
    var typeDownloadProgress: TypeDownloadProgress = TypeDownloadProgress.NOT_DOWNLOADED
)