package my.zukoap.composablechat.domain.use_cases

import android.graphics.Bitmap
import my.zukoap.composablechat.domain.entity.file.TypeDownloadProgress
import my.zukoap.composablechat.domain.entity.file.TypeUpload
import my.zukoap.composablechat.domain.repository.FileRepository
import my.zukoap.composablechat.domain.repository.MessageRepository
import javax.inject.Inject
import my.zukoap.composablechat.domain.entity.file.File as DomainFile
import java.io.File as IOFile

class FileUseCase(
    private val fileRepository: FileRepository,
    private val messageRepository: MessageRepository,
    private val visitorUseCase: VisitorUseCase
) {

    fun uploadFile(file: DomainFile, handleUploadFile: (responseCode: Int, responseMessage: String) -> Unit) {
        val visitor = visitorUseCase.getVisitor() ?: return
        fileRepository.uploadFile(visitor, file, TypeUpload.MULTIPART, handleUploadFile)
    }

    fun uploadImage(bitmap: Bitmap, handleUploadFile: (responseCode: Int, responseMessage: String) -> Unit) {
        val visitor = visitorUseCase.getVisitor() ?: return
        fileRepository.uploadMediaFile(visitor, bitmap, TypeUpload.MULTIPART, handleUploadFile)
    }

    fun uploadFiles(listFile: List<DomainFile>, handleUploadFile: (responseCode: Int, responseMessage: String) -> Unit) {
        listFile.forEach {
            uploadFile(it, handleUploadFile)
        }
    }

    suspend fun downloadDocument(
        id: String,
        documentName: String,
        documentUrl: String,
        directory: IOFile,
        openDocument: suspend (file: IOFile) -> Unit,
        downloadedFail: () -> Unit
    ) {
        val documentFile = IOFile(directory, "${id}_${documentName}")
        val alternativeFile = IOFile(directory, "${id}.${documentName.split(".").last()}")

        when {
            documentFile.exists() -> openDocument(documentFile)
            alternativeFile.exists() -> openDocument(alternativeFile)
            else -> {
                messageRepository.updateTypeDownloadProgressOfMessageWithAttachment(id, TypeDownloadProgress.DOWNLOADING)
                fileRepository.downloadDocument(
                    documentUrl = documentUrl,
                    documentFile = documentFile,
                    alternativeFile = alternativeFile,
                    downloadedSuccess = {
                        messageRepository.updateTypeDownloadProgressOfMessageWithAttachment(id, TypeDownloadProgress.DOWNLOADED)
                        openDocument(documentFile)
                    },
                    downloadedFail = {
                        messageRepository.updateTypeDownloadProgressOfMessageWithAttachment(id, TypeDownloadProgress.NOT_DOWNLOADED)
                        downloadedFail()
                    }
                )
            }
        }
    }

}