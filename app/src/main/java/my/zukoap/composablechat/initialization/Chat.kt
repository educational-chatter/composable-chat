package my.zukoap.composablechat.initialization

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import my.zukoap.composablechat.R
import my.zukoap.composablechat.common.*
import my.zukoap.composablechat.di.KoinSdkComponent
import my.zukoap.composablechat.domain.entity.auth.Visitor
import my.zukoap.composablechat.domain.use_cases.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

object Chat: KoinComponent {

    private val job = Job()
    private val scopeIO = CoroutineScope(Dispatchers.IO + job)
    private val scopeUI = CoroutineScope(Dispatchers.Main + job)

    private val conditionUseCase:ConditionUseCase by inject()
  //  private val visitorUseCase: VisitorUseCase by inject()
    private val authUseCase: AuthUseCase by inject()
/*    private val notificationUseCase: NotificationUseCase by inject()
    private val personUseCase: PersonUseCase by inject()*/
    //private val sdkComponent: KoinSdkComponent? = KoinSdkComponent()

    //internal fun getSdkComponent(): KoinSdkComponent = KoinSdkComponent()
        ?: throw IllegalStateException("You must call the init method before going to the chat.")

    //TODO check where getSdkComponent() is used

    fun setOnChatMessageListener(listener: ChatMessageListener) {
        conditionUseCase?.setMessageListener(listener)
    }

/*    private fun initDI(context: Context) {
        if (sdkComponent == null) {
            sdkComponent = KoinSdkComponent()
        }
        conditionUseCase = ConditionUseCase(sdkComponent!!.conditionRepository)
        visitorUseCase = VisitorUseCase(sdkComponent!!.visitorRepository)
        personUseCase = PersonUseCase(sdkComponent!!.personRepository)
        notificationUseCase =
            NotificationUseCase(sdkComponent!!.notificationRepository, visitorUseCase!!)
        authUseCase = AuthUseCase(
            sdkComponent!!.authRepository,
            visitorUseCase!!,
            conditionUseCase!!,
            personUseCase!!,
            notificationUseCase!!
        )
    }*/

    fun init(
        context: Context,
        urlChatScheme: String,
        urlChatHost: String,
        urlChatNameSpace: String,
        authType: AuthType = AuthType.AUTH_WITHOUT_FORM,
        initialMessageMode: InitialMessageMode? = InitialMessageMode.SEND_ON_OPEN,
        operatorPreviewMode: OperatorPreviewMode = OperatorPreviewMode.CACHE,
        operatorNameMode: OperatorNameMode = OperatorNameMode.IMMUTABLE,
        clickableLinkMode: ClickableLinkMode = ClickableLinkMode.ALL,
        localeLanguage: String = context.getString(R.string.default_language),
        localeCountry: String = context.getString(R.string.default_country),
        phonePatterns: Array<CharSequence> = context.resources.getTextArray(R.array.phone_patterns),
        uploadPoolMessagesTimeout: Long? = null,
        fileProviderAuthorities: String? = null,
        certificatePinning: String? = null,
        fileConnectTimeout: Long? = null,
        fileReadTimeout: Long? = null,
        fileWriteTimeout: Long? = null,
        fileCallTimeout: Long? = null
    ) {
        ChatParams.authMode = authType
        ChatParams.initialMessageMode = initialMessageMode
        ChatParams.urlChatScheme = urlChatScheme
        ChatParams.urlChatHost = urlChatHost
        ChatParams.urlChatNameSpace = urlChatNameSpace
        ChatParams.operatorPreviewMode = operatorPreviewMode
        ChatParams.operatorNameMode = operatorNameMode
        ChatParams.clickableLinkMode = clickableLinkMode
        ChatParams.locale = Locale(localeLanguage, localeCountry)
        ChatParams.phonePatterns = phonePatterns
        uploadPoolMessagesTimeout?.let { ChatParams.uploadPoolMessagesTimeout = it }
        ChatParams.fileProviderAuthorities = fileProviderAuthorities
        ChatParams.certificatePinning = certificatePinning
        ChatParams.fileConnectTimeout = fileConnectTimeout
        ChatParams.fileReadTimeout = fileReadTimeout
        ChatParams.fileWriteTimeout = fileWriteTimeout
        ChatParams.fileCallTimeout = fileCallTimeout
       // initDI(context)
    }

    fun createSession() {
        conditionUseCase?.createSessionChat()
    }

    fun destroySession() {
        conditionUseCase?.destroySessionChat()
    }

    fun wakeUp(visitor: Visitor?) {
        conditionUseCase?.openApp()
        authUseCase?.logIn(
            visitor = visitor
        )
    }

    fun wakeUp() {
        conditionUseCase?.openApp()
    }

    fun drop() {
        conditionUseCase?.closeApp()
        conditionUseCase?.dropChat()
    }

    fun logOut(context: Context) {
        scopeIO.launch {
            authUseCase?.logOut(context.filesDir)
        }
    }

    fun logOutWithUIActionAfter(context: Context, actionUIAfterLogOut: () -> Unit) {
        scopeIO.launch {
            authUseCase?.logOut(context.filesDir)
            scopeUI.launch {
                actionUIAfterLogOut()
            }
        }
    }

    fun logOutWithIOActionAfter(context: Context, actionIOAfterLogOut: () -> Unit) {
        scopeIO.launch {
            authUseCase?.logOut(context.filesDir)
            actionIOAfterLogOut()
        }
    }

}