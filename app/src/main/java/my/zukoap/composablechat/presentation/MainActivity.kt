package my.zukoap.composablechat.presentation

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import my.zukoap.composablechat.R
import my.zukoap.composablechat.initialization.Chat
import my.zukoap.composablechat.initialization.ChatMessageListener
import my.zukoap.composablechat.presentation.chat.ComposableChatScreen
import my.zukoap.composablechat.presentation.ui.theme.ComposableChatTheme


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class, ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val visitor = getVisitor(this)
        Chat.init(
            this,
            getString(R.string.urlChatScheme),
            getString(R.string.urlChatHost),
            getString(R.string.urlChatNameSpace),
            fileProviderAuthorities = getString(R.string.chat_file_provider_authorities)
        )
        Chat.createSession()
        Chat.setOnChatMessageListener(object : ChatMessageListener {
            override fun getNewMessages(countMessages: Int) {
                Log.d("TEST_GET_MSG", "get new messages count - ${countMessages};")
            }
        })
        Chat.wakeUp(visitor)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) // It is needed to resize the application window when keyboard appears, otherwise it overlaps bottom input field
        setContent {
            ComposableChatTheme {
                val isFirstLaunch = remember { mutableStateOf(true) }
                ComposableChatScreen(visitor = visitor, isFirstLaunch = isFirstLaunch)
            }
        }
    }
}
