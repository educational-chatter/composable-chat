package my.zukoap.composablechat.data.helper.file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import my.zukoap.composablechat.data.ContentTypeValue
import my.zukoap.composablechat.data.helper.converters.file.convertToBase64
import my.zukoap.composablechat.data.helper.converters.file.convertToFile
import my.zukoap.composablechat.domain.entity.file.TypeFile
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class RequestHelper(
    private val context: Context
) {

    fun generateMultipartRequestBody(uri: Uri): RequestBody? {
        return context.contentResolver.openInputStream(uri)?.readBytes()?.let { bytes ->
            RequestBody.create(
                MultipartBody.FORM,
                bytes
            )
        }
    }

    fun generateMultipartRequestBody(file: File): RequestBody? {
        return file.readBytes().let { bytes ->
            RequestBody.create(
                MultipartBody.FORM,
                bytes
            )
        }
    }

    fun generateMultipartRequestBody(bitmap: Bitmap, mediaName: String): RequestBody? {
        return convertToFile(bitmap, context, mediaName).readBytes().let { bytes ->
            RequestBody.create(
                ContentTypeValue.MEDIA.value.toMediaType(),
                bytes
            )
        }
    }

    fun generateJsonRequestBody(uri: Uri, type: TypeFile): String? {
        return when(type) {
            TypeFile.FILE -> context.contentResolver.openInputStream(uri)?.run(::convertToBase64)
            TypeFile.IMAGE -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    generateJsonRequestBody(
                        ImageDecoder.decodeBitmap(source)
                    )
                } else {
                    generateJsonRequestBody(
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    )
                }
            }
            else -> null
        }
    }

    fun generateJsonRequestBody(bitmap: Bitmap): String = convertToBase64(bitmap)

}