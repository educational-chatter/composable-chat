package my.zukoap.composablechat.domain.entity.file

import android.net.Uri

data class File(
    val uri: Uri,
    val type: TypeFile
)