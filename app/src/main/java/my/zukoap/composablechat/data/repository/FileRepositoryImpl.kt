package my.zukoap.composablechat.data.repository

import android.graphics.Bitmap
import android.util.Log
import my.zukoap.composablechat.data.ApiParams
import my.zukoap.composablechat.data.ContentTypeValue
import my.zukoap.composablechat.data.helper.file.FileInfoHelper
import my.zukoap.composablechat.data.helper.file.RequestHelper
import my.zukoap.composablechat.data.local.db.dao.FileDao
import my.zukoap.composablechat.data.local.db.entity.FileEntity
import my.zukoap.composablechat.data.remote.rest.FileApi
import my.zukoap.composablechat.domain.entity.auth.Visitor
import my.zukoap.composablechat.domain.entity.file.NetworkBodyStructureUploadFile
import my.zukoap.composablechat.domain.entity.file.TypeUpload
import my.zukoap.composablechat.domain.repository.FileRepository
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import javax.inject.Inject
import my.zukoap.composablechat.domain.entity.file.File as FileModel

class FileRepositoryImpl(
    private val fileApi: FileApi,
    private val fileDao: FileDao,
    private val fileInfoHelper: FileInfoHelper,
    private val fileRequestHelper: RequestHelper
) : FileRepository {

    private fun uploadFile(uuid: String, token: String, fileName: String, fileRequestBody: String, handleUploadFile: (responseCode: Int, responseMessage: String) -> Unit) {
        val request = fileApi.uploadFile(
            visitorToken = token,
            networkBody = NetworkBodyStructureUploadFile(
                fileName = fileName,
                uuid = uuid,
                fileB64 = fileRequestBody
            )
        )

        request.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                handleUploadFile(response.code(), response.message())
                Log.d("UPLOAD_TEST", "Success upload - ${response.message()} ${response.body()}; ${response.code()}; ${request.request().url}")
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                when (t.message) {
                    TIMEOUT_CONST -> handleUploadFile(TIMEOUT_CODE, "")
                }
                Log.d("UPLOAD_TEST", "Fail upload! - ${t.message};")
            }
        })
    }

    private fun uploadFile(uuid: String, token: String, fileName: String, fileRequestBody: RequestBody, handleUploadFile: (responseCode: Int, responseMessage: String) -> Unit) {
        val body: MultipartBody.Part = MultipartBody.Part.createFormData(ApiParams.FILE_FIELD_NAME, fileName, fileRequestBody)
        val fileNameRequestBody = RequestBody.create(
            ContentTypeValue.TEXT_PLAIN.value.toMediaType(),
            fileName
        )
        val uuidRequestBody = RequestBody.create(
            ContentTypeValue.TEXT_PLAIN.value.toMediaType(),
            uuid
        )

        val request = fileApi.uploadFile(
            visitorToken = token,
            fileName = fileNameRequestBody,
            uuid = uuidRequestBody,
            fileB64 = body
        )

        request.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                handleUploadFile(response.code(), response.message())
                Log.d("UPLOAD_TEST", "Success upload - ${response.message()} ${response.body()}; ${response.code()}; ${request.request().url}")
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                when (t.message) {
                    TIMEOUT_CONST -> handleUploadFile(TIMEOUT_CODE, "")
                }
                Log.d("UPLOAD_TEST", "Fail upload! - ${t.message};")
            }
        })
    }

    override fun uploadFile(visitor: Visitor, file: FileModel, type: TypeUpload, handleUploadFile: (responseCode: Int, responseMessage: String) -> Unit) {
        val fileName = fileInfoHelper.getFileName(file.uri) ?: return
        fileDao.addFile(FileEntity(visitor.uuid, fileName))
        when (type) {
            TypeUpload.JSON -> {
                val fileRequestBody = fileRequestHelper.generateJsonRequestBody(file.uri, file.type) ?: return
                uploadFile(visitor.uuid, visitor.token, fileName, fileRequestBody, handleUploadFile)
            }
            TypeUpload.MULTIPART -> {
                val fileRequestBody = fileRequestHelper.generateMultipartRequestBody(file.uri) ?: return
                uploadFile(visitor.uuid, visitor.token, fileName, fileRequestBody, handleUploadFile)
            }
        }
    }

    override fun uploadMediaFile(visitor: Visitor, bitmap: Bitmap, type: TypeUpload, handleUploadFile: (responseCode: Int, responseMessage: String) -> Unit) {
        val fileName = "createPhoto${System.currentTimeMillis()}.jpg"
        when (type) {
            TypeUpload.JSON -> {
                val fileRequestBody = fileRequestHelper.generateJsonRequestBody(bitmap)
                uploadFile(visitor.uuid, visitor.token, fileName, fileRequestBody, handleUploadFile)
            }
            TypeUpload.MULTIPART -> {
                val fileRequestBody = fileRequestHelper.generateMultipartRequestBody(bitmap, fileName) ?: return
                uploadFile(visitor.uuid, visitor.token, fileName, fileRequestBody, handleUploadFile)
            }
        }
    }

    override suspend fun downloadDocument(documentUrl: String, documentFile: File, alternativeFile: File, downloadedSuccess: suspend () -> Unit, downloadedFail: () -> Unit) {
        val correctFile = try {
            documentFile.createNewFile()
            documentFile
        } catch (ex: IOException) {
            try {
                alternativeFile.createNewFile()
                alternativeFile
            } catch (ex: IOException) {
                downloadedFail()
                return
            }
        }
        try {
            val url = URL(documentUrl)
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connect()
            val inputStream = urlConnection.inputStream
            val fileOutputStream = FileOutputStream(correctFile)

            val buffer = ByteArray(MEGABYTE)
            var bufferLength: Int
            while (inputStream.read(buffer).also { bufferLength = it } > 0) {
                fileOutputStream.write(buffer, 0, bufferLength)
            }
            fileOutputStream.close()

            downloadedSuccess()
        } catch (ex: FileNotFoundException) {
            downloadedFail()
        } catch (ex: MalformedURLException) {
            downloadedFail()
        } catch (ex: IOException) {
            downloadedFail()
        }
    }

    companion object {
        private const val TIMEOUT_CODE = 408
        private const val TIMEOUT_CONST = "timeout"
        private const val MEGABYTE = 1024 * 1024
    }

}