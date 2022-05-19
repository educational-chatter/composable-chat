package my.zukoap.composablechat

import android.util.Log
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import my.zukoap.composablechat.initialization.Chat
import my.zukoap.composablechat.initialization.ChatMessageListener
import my.zukoap.composablechat.presentation.MainActivity
import my.zukoap.composablechat.presentation.chat.ComposableChatScreen
import my.zukoap.composablechat.presentation.chat.components.UserInput
import my.zukoap.composablechat.presentation.getVisitor
import my.zukoap.composablechat.presentation.ui.theme.ComposableChatTheme
import org.junit.Rule
import org.junit.Test

class UserInputTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalComposeUiApi::class)
    @Test
    fun differentButtonsOnEmptyAndNotInputField() {
        composeTestRule.setContent {
            UserInput(sendMessage = { msg: String, id: String? ->} )
        }
        composeTestRule.onNodeWithContentDescription("Attach file button").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Send button").assertDoesNotExist()
        Thread.sleep(2000)
        composeTestRule.onNodeWithText("Сообщение").performClick().performTextInput("hello")
        composeTestRule.onNodeWithContentDescription("Send button").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Attach file button").assertDoesNotExist()
        Thread.sleep(2000)
    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalCoroutinesApi::class)
    @Test
    fun different() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val visitor = getVisitor(appContext)
        Chat.init(
            appContext,
            appContext.getString(R.string.urlChatScheme),
            appContext.getString(R.string.urlChatHost),
            appContext.getString(R.string.urlChatNameSpace),
            fileProviderAuthorities = appContext.getString(R.string.chat_file_provider_authorities)
        )
        Chat.createSession()
        Chat.setOnChatMessageListener(object : ChatMessageListener {
            override fun getNewMessages(countMessages: Int) {
                Log.d("TEST_GET_MSG", "get new messages count - ${countMessages};")
            }
        })
        Chat.wakeUp(visitor)
        //window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) // It is needed to resize the application window when keyboard appears, otherwise it overlaps bottom input field
        composeTestRule.setContent {
            ComposableChatTheme {
                val isFirstLaunch = remember { mutableStateOf(true) }
                ComposableChatScreen(visitor = visitor, isFirstLaunch = isFirstLaunch)
            }
        }
        composeTestRule.onRoot().printToLog("KEYBOARD")
        Thread.sleep(5000)
        composeTestRule.onNodeWithText("rest api").assertHasClickAction().assertIsEnabled()
        composeTestRule.onNodeWithText("rest api").performTouchInput {swipeDown(endY = 200F)}
        Thread.sleep(5000)
//        composeTestRule.onNodeWithText("Поехали! \uD83D\uDE80").assertHasClickAction().assertIsNotEnabled()
        composeTestRule.onNodeWithText("Завершить диалог").assertHasClickAction().assertIsNotEnabled()
        // Thread.sleep(2000)
    }
}