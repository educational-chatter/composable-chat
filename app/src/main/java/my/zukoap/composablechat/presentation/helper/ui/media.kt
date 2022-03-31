package my.zukoap.composablechat.presentation.helper.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import coil.imageLoader
import coil.request.ImageRequest
import coil.size.Size
import java.net.URL
import kotlin.math.min

fun getSizeMediaFile(context: Context, url: String, resultSize: (height: Int?, width: Int?) -> Unit) {
    val request = ImageRequest.Builder(context)
        .data(url)
        .target(
            onStart = { placeholder ->
                // Handle the placeholder drawable.
            },
            onSuccess = { result ->
                // Handle the successful result.
                val bitmap = (result as BitmapDrawable).bitmap
                resultSize(bitmap.height, bitmap.width)
            },
            onError = { error ->
                // Handle the error drawable.
                resultSize(null, null)
            }
        )
        .build()
    context.imageLoader.enqueue(request)
}

suspend fun getSizeMediaFile(context: Context, url: String): Pair<Int, Int>? {
    return try {
        val request = ImageRequest.Builder(context)
            .data(url)
            .size(Size.ORIGINAL)
            .build()
        val drawable = context.imageLoader.execute(request).drawable
        val bitmap = (drawable as BitmapDrawable).bitmap
        Pair(bitmap.height, bitmap.width)
    } catch (ex: Exception) {
        null
    }
}

fun getWeightFile(urlPath: String): Long? {
    val CONTENT_DISPOSITION = "content-disposition"
    val template = "size="

    return try {
        val url = URL(urlPath)
        val urlConnection = url.openConnection()
        urlConnection.connect()
        val size = urlConnection.contentLength

        if (size == -1) {
            val contentDisposition = urlConnection.getHeaderField(CONTENT_DISPOSITION)
            if (contentDisposition == null) {
                null
            } else {
                val startIndex = contentDisposition.indexOf(template) + template.length
                val indexEndComma = contentDisposition.indexOf(",", startIndex)
                val indexEndBracket = contentDisposition.indexOf("]", startIndex)
                val alternativeSize = (when {
                    startIndex != -1 && indexEndComma != -1 && indexEndBracket != -1 -> contentDisposition.substring(startIndex, min(indexEndComma, indexEndBracket))
                    startIndex != -1 && indexEndComma != -1 && indexEndBracket == -1 -> contentDisposition.substring(startIndex, indexEndComma)
                    startIndex != -1 && indexEndComma == -1 && indexEndBracket != -1 -> contentDisposition.substring(startIndex, indexEndBracket)
                    startIndex != -1 && indexEndComma == -1 && indexEndBracket == -1 -> contentDisposition.substring(startIndex)
                    else -> null
                })?.toLong()
                alternativeSize
            }
        } else {
            size.toLong()
        }
    } catch (ex: Exception) {
        null
    }
}