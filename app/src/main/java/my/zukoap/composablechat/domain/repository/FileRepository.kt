package my.zukoap.composablechat.domain.repository

import android.graphics.Bitmap
import my.zukoap.composablechat.domain.entity.auth.Visitor
import my.zukoap.composablechat.domain.entity.file.TypeUpload
import my.zukoap.composablechat.domain.entity.file.File as DomainFile
import java.io.File as IOFile


interface FileRepository {
    fun uploadFile(visitor: Visitor, file: DomainFile, type: TypeUpload, handleUploadFile: (responseCode: Int, responseMessage: String) -> Unit)
    fun uploadMediaFile(visitor: Visitor, bitmap: Bitmap, type: TypeUpload, handleUploadFile: (responseCode: Int, responseMessage: String) -> Unit)
    suspend fun downloadDocument(documentUrl: String, documentFile: IOFile, alternativeFile: IOFile, downloadedSuccess: suspend () -> Unit, downloadedFail: () -> Unit)
}