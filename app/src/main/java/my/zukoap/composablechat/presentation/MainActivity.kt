package my.zukoap.composablechat.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import my.zukoap.composablechat.R
import my.zukoap.composablechat.initialization.Chat
import my.zukoap.composablechat.initialization.ChatMessageListener
import my.zukoap.composablechat.presentation.chat.ComposableChatScreen
import my.zukoap.composablechat.presentation.chat.components.ErrorScreen
import my.zukoap.composablechat.presentation.helper.network.ConnectionState
import my.zukoap.composablechat.presentation.helper.network.connectivityState
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
        setContent {
            ComposableChatTheme {
                // A surface container using the 'background' color from the theme
                //Chat.wakeUp(getVisitor(this))
                val isFirstLaunch = remember{ mutableStateOf(true) }
                ComposableChatScreen(visitor = visitor, isFirstLaunch = isFirstLaunch)
                //Text(text = "Hi")
            }
        }
    }
}
