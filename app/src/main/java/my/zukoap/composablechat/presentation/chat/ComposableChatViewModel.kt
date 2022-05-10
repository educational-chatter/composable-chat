package my.zukoap.composablechat.presentation.chat

//import my.zukoap.composablechat.data.paging.ChatRemoteMediator
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.plus
import my.zukoap.composablechat.domain.entity.auth.Visitor
import my.zukoap.composablechat.domain.entity.internet.InternetConnectionState
import my.zukoap.composablechat.domain.use_cases.*
import my.zukoap.composablechat.presentation.*
import my.zukoap.composablechat.presentation.base.BaseViewModel
import my.zukoap.composablechat.presentation.chat.model.MessageModel
import my.zukoap.composablechat.presentation.chat.model.SeparateItem
import my.zukoap.composablechat.presentation.helper.mappers.messageModelMapper
import java.text.SimpleDateFormat
import my.zukoap.composablechat.domain.entity.file.File as DomainFile
import java.io.File as IOFile


class ComposableChatViewModel(
    private val authChatUseCase: AuthUseCase,
    private val messageUseCase: MessageUseCase,
    private val fileUseCase: FileUseCase,
    private val conditionUseCase: ConditionUseCase,
    private val feedbackUseCase: FeedbackUseCase,
    private val configurationUseCase: ConfigurationUseCase,
    //private val savedStateHandle: SavedStateHandle,
    private val context: Context,
) : BaseViewModel() {

    var currentReadMessageTime = conditionUseCase.getCurrentReadMessageTime()
    var isAllHistoryLoaded = conditionUseCase.checkFlagAllHistoryLoaded()
    var initialLoadKey = conditionUseCase.getInitialLoadKey()

    /*TODO livedata -> state*/

    private var _countUnreadMessages = MutableLiveData<Int>()
    val countUnreadMessages: LiveData<Int> = _countUnreadMessages

    private var _scrollToDownVisible = MutableLiveData(false)
    val scrollToDownVisible: LiveData<Boolean> = _scrollToDownVisible

    private var _feedbackContainerVisible = MutableLiveData(false)
    val feedbackContainerVisible: LiveData<Boolean> = _feedbackContainerVisible

    private var _openDocument = MutableLiveData<Pair<IOFile?, Boolean>?>()
    val openDocument: LiveData<Pair<IOFile?, Boolean>?> = _openDocument

    private var _mergeHistoryBtnVisible = MutableLiveData(false)
    val mergeHistoryBtnVisible: LiveData<Boolean> = _mergeHistoryBtnVisible

    private var _mergeHistoryProgressVisible = MutableLiveData(false)
    val mergeHistoryProgressVisible: LiveData<Boolean> = _mergeHistoryProgressVisible

    private val formatTime = SimpleDateFormat("dd.MM.yyyy")
    private val ioDispatcher = Dispatchers.IO
    private val pager = messageUseCase.getPager(ioDispatcher)

    @OptIn(ExperimentalPagingApi::class)
    private var _uploadMessagesForUser: Flow<PagingData<MessageModel>> =
        pager.flow.map { pagingData ->
            pagingData.map { message ->
                messageModelMapper(message)
            }.insertSeparators<MessageModel, MessageModel> { before, after ->
                if (after == null) {
                    // we're at the end of the list
                    return@insertSeparators null
                }

                if (before == null) {
                    // we're at the beginning of the list
                    return@insertSeparators null
                }
                if (formatTime.format(before.timestamp) != formatTime.format(after.timestamp)) {
                    return@insertSeparators SeparateItem(before.timestamp)
                } else {
                    // no separator
                    null
                }
            }
        }.filterNotNull().distinctUntilChanged()
            .cachedIn(viewModelScope) // .filterNotNull() may be redundant here
    val uploadMessagesForUser: Flow<PagingData<MessageModel>> = _uploadMessagesForUser

    private var _replyMessage: MutableLiveData<MessageModel?> = MutableLiveData(null)
    val replyMessage: LiveData<MessageModel?> = _replyMessage
    private var _replyMessagePosition: MutableLiveData<Int?> = MutableLiveData(null)
    val replyMessagePosition: LiveData<Int?> = _replyMessagePosition


    @OptIn(ExperimentalPagingApi::class)
    fun uploadMessages() {
/*        val pagingSourceFactory = { messageUseCase.getAllMessages() }
        val pager = Pager(
            config = PagingConfig(ChatParams.pageSize, enablePlaceholders = false),
            initialKey = initialLoadKey,
            remoteMediator = ChatRemoteMediator(chatDatabase = db, messageUseCase = messageUseCase, conditionUseCase = conditionUseCase, syncMessagesAcrossDevices = ::syncMessagesAcrossDevices),
            pagingSourceFactory = pagingSourceFactory
        )
        val formatTime = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        _uploadMessagesForUser = pager.flow.map { pagingData ->
            pagingData.map { message ->
                messageModelMapper(message)
            }
        }*/
    }


    private fun syncMessagesAcrossDevices(indexFirstUnreadMessage: Int) {
        initialLoadKey = indexFirstUnreadMessage
        uploadMessages()
    }

    private fun deliverMessagesToUser() {
        launchIO {
            if (uploadMessagesForUser.firstOrNull() == null) {
                uploadMessages()
            }
        }
    }

    private val eventAllHistoryLoaded: () -> Unit = {
        isAllHistoryLoaded = true
    }
    private val sync: suspend () -> Unit = {
        launchUI { chatStateListener?.startSynchronization() }
        _displayableUIObject.postValue(DisplayableUIObject.SYNCHRONIZATION)
        messageUseCase.syncMessages(
            updateReadPoint = updateCurrentReadMessageTime,
            syncMessagesAcrossDevices = ::syncMessagesAcrossDevices,
            eventAllHistoryLoaded = eventAllHistoryLoaded
        )
    }
    private val updateCurrentReadMessageTime: (Long) -> Boolean = { newTimeMark ->
        if (newTimeMark > currentReadMessageTime) {
            currentReadMessageTime = newTimeMark
            true
        } else {
            false
        }
    }


    private var _internetConnectionState: MutableLiveData<InternetConnectionState> =
        MutableLiveData()
    val internetConnectionState: LiveData<InternetConnectionState> = _internetConnectionState
    private var _displayableUIObject = MutableLiveData(DisplayableUIObject.NOTHING)
    val displayableUIObject: LiveData<DisplayableUIObject> = _displayableUIObject
    var clientInternetConnectionListener: ChatInternetConnectionListener? = null

    var mergeHistoryListener: MergeHistoryListener = object : MergeHistoryListener {
        override fun showDialog() {
            _mergeHistoryProgressVisible.postValue(false)
            _mergeHistoryBtnVisible.postValue(true)
        }

        override fun startMerge() {
            _mergeHistoryBtnVisible.postValue(false)
            _mergeHistoryProgressVisible.postValue(true)
        }

        override fun endMerge() {
            _mergeHistoryProgressVisible.postValue(false)
            _mergeHistoryBtnVisible.postValue(false)
        }
    }

    var chatStateListener: ChatStateListener? = null
    private val internetConnectionListener = object : ChatInternetConnectionListener {
        override fun connect() {
            launchUI { clientInternetConnectionListener?.connect() }
            _internetConnectionState.postValue(InternetConnectionState.HAS_INTERNET)
        }

        override fun failConnect() {
            launchUI { clientInternetConnectionListener?.failConnect() }
            _internetConnectionState.postValue(InternetConnectionState.NO_INTERNET)
        }

        override fun lossConnection() {
            launchUI { clientInternetConnectionListener?.lossConnection() }
            _internetConnectionState.postValue(InternetConnectionState.NO_INTERNET)
        }

        override fun reconnect() {
            launchUI { clientInternetConnectionListener?.reconnect() }
            _internetConnectionState.postValue(InternetConnectionState.RECONNECT)
        }
    }

    private val chatEventListener = object : ChatEventListener {
        override fun operatorStartWriteMessage() {
            _displayableUIObject.postValue(DisplayableUIObject.OPERATOR_START_WRITE_MESSAGE)
        }

        override fun operatorStopWriteMessage() {
            _displayableUIObject.postValue(DisplayableUIObject.OPERATOR_STOP_WRITE_MESSAGE)
        }

        override fun finishDialog() {
            _feedbackContainerVisible.postValue(true)
        }

        override fun showUploadHistoryBtn() {
            mergeHistoryListener.showDialog()
        }

        override fun synchronized() {
            launchUI { chatStateListener?.endSynchronization() }
            _displayableUIObject.postValue(DisplayableUIObject.CHAT)
        }
    }

    var uploadFileListener: UploadFileListener? = null

    init {
        conditionUseCase.setInternetConnectionListener(internetConnectionListener)
    }

    fun initOnInternet() { // Look up for LaunchedEffect(true) in ComposableChatScreen for info
        conditionUseCase.goToChatScreen()
        launchIO {
            configurationUseCase.getConfiguration()
        }
    }

    fun onStartChat(visitor: Visitor?) {
        launchUI {
//            delay(ChatAttr.getInstance().timeDelayed)
            authChatUseCase.logIn(
                visitor = visitor,
                successAuthUi = ::deliverMessagesToUser,
                sync = sync,
                failAuthUi = { _displayableUIObject.postValue(DisplayableUIObject.WARNING) },
                firstLogInWithForm = { _displayableUIObject.value = DisplayableUIObject.FORM_AUTH },
                updateCurrentReadMessageTime = updateCurrentReadMessageTime,
                chatEventListener = chatEventListener
            )
        }
    }

    fun onStop() {
        currentReadMessageTime.run(conditionUseCase::saveCurrentReadMessageTime)
        countUnreadMessages.value?.run(conditionUseCase::saveCountUnreadMessages)
    }

    override fun onCleared() {
        super.onCleared()
        conditionUseCase.leaveChatScreen()
        removeAllInfoMessages()
    }

    fun registration(vararg args: String) {
        launchUI {
//            delay(ChatAttr.getInstance().timeDelayed)
            authChatUseCase.logIn(
                visitor = Visitor.map(args),
                successAuthUi = ::deliverMessagesToUser,
                sync = sync,
                failAuthUi = { _displayableUIObject.postValue(DisplayableUIObject.WARNING) },
                updateCurrentReadMessageTime = updateCurrentReadMessageTime,
                chatEventListener = chatEventListener
            )
        }
    }

    fun reload() {
        launchUI {
//            delay(ChatAttr.getInstance().timeDelayed)
            authChatUseCase.logIn(
                successAuthUi = ::deliverMessagesToUser,
                sync = sync,
                failAuthUi = { _displayableUIObject.postValue(DisplayableUIObject.WARNING) },
                updateCurrentReadMessageTime = updateCurrentReadMessageTime,
                chatEventListener = chatEventListener
            )
        }
    }

    fun uploadOldMessages(uploadHistoryComplete: () -> Unit = {}, executeAnyway: Boolean = false) {
        launchIO {
            messageUseCase.uploadHistoryMessages(
                eventAllHistoryLoaded = eventAllHistoryLoaded,
                uploadHistoryComplete = uploadHistoryComplete,
                executeAnyway = executeAnyway
            )
        }
    }

    fun downloadOrOpenDocument(
        id: String,
        documentName: String,
        documentUrl: String
    ) {
        launchIO {
            fileUseCase.downloadDocument(
                id = id,
                documentName = documentName,
                documentUrl = documentUrl,
                directory = context.filesDir,
                openDocument = { documentFile ->
                    delay(1000L)
                    _openDocument.postValue(Pair(documentFile, true))
                },
                downloadedFail = {
                    _openDocument.postValue(Pair(null, false))
                }
            )
        }
    }

/*    fun openImage(activity: Activity, imageName: String, imageUrl: String, downloadFun: (fileName: String, fileUrl: String, fileType: TypeFile) -> Unit) {
        ShowImageDialog.Builder(activity)
            .setName(imageName)
            .setUrl(imageUrl)
            .setType(TypeFile.IMAGE)
            .setFunDownload(downloadFun)
            .show()
    }

    fun openGif(activity: Activity, gifName: String, gifUrl: String, downloadFun: (fileName: String, fileUrl: String, fileType: TypeFile) -> Unit) {
        ShowImageDialog.Builder(activity)
            .setName(gifName)
            .setUrl(gifUrl)
            .setType(TypeFile.GIF)
            .setFunDownload(downloadFun)
            .show()
    }*/

    fun selectAction(messageId: String, actionId: String) {
        launchIO {
            messageUseCase.selectActionInMessage(messageId, actionId)
        }
    }

    fun selectReplyMessage(messageId: String) {
        launchIO {
            _replyMessagePosition.postValue(
                messageUseCase.getCountMessagesInclusiveTimestampById(
                    messageId
                )
            )
        }
    }

    fun giveFeedbackOnOperator(countStars: Int) {
        launchIO {
            feedbackUseCase.giveFeedbackOnOperator(countStars)
        }
    }

    fun updateData(id: String, height: Int, width: Int) {
        launchIO {
            messageUseCase.updateSizeMessage(id, height, width)
        }
    }

    fun sendMessage(message: String, repliedMessageId: String?) {
        launchIO {
            messageUseCase.sendMessage(
                message = message,
                repliedMessageId = repliedMessageId
            )
        }
    }

    fun sendFile(file: DomainFile) {
        launchIO {
            fileUseCase.uploadFile(file) { responseCode, responseMessage ->
                uploadFileListener?.let { listener ->
                    handleUploadFile(
                        listener,
                        responseCode,
                        responseMessage
                    )
                }
            }
        }
    }

    fun sendFiles(fileList: List<DomainFile>) {
        launchIO {
            fileUseCase.uploadFiles(fileList) { responseCode, responseMessage ->
                uploadFileListener?.let { listener ->
                    handleUploadFile(
                        listener,
                        responseCode,
                        responseMessage
                    )
                }
            }
        }
    }

    fun readMessage(lastTimestamp: Long?) {
        val isReadNewMessage = lastTimestamp?.run(updateCurrentReadMessageTime) ?: false
        if (isReadNewMessage) {
            updateCountUnreadMessages()
        }
    }

    fun updateCountUnreadMessages(
        timestampLastMessage: Long? = null,
        actionUiAfter: (Int) -> Unit = {}
    ) {
        launchIO {
            val unreadMessagesCount =
                messageUseCase.getCountUnreadMessages(currentReadMessageTime, timestampLastMessage)
            unreadMessagesCount?.run(_countUnreadMessages::postValue)
            launchUI {
                unreadMessagesCount?.run(actionUiAfter)
            }
        }
    }

    private fun removeAllInfoMessages() {
        launchIO {
            messageUseCase.removeAllInfoMessages()
        }
    }
}
