package my.zukoap.composablechat.presentation.chat

//import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import my.zukoap.composablechat.domain.entity.auth.Visitor
import my.zukoap.composablechat.presentation.chat.components.ComposableChatAppBar
import my.zukoap.composablechat.presentation.chat.components.InfoMessage
import my.zukoap.composablechat.presentation.chat.components.TextMessage
import my.zukoap.composablechat.presentation.chat.model.*
import org.koin.androidx.compose.viewModel
import java.lang.IndexOutOfBoundsException

@Composable
fun ComposableChatScreen(
    visitor: Visitor? = null
) {
    Scaffold(
        topBar = {
            ComposableChatAppBar(title = { androidx.compose.material3.Text("Поддержка") })
        }
    ) {
        val composableViewModel: ComposableChatViewModel by viewModel()
        val listState: LazyListState = rememberLazyListState()
        var isFirstUploadMessages by remember { mutableStateOf(true) }
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(key1 = lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    composableViewModel.onStartChat(null)
                } else if (event == Lifecycle.Event.ON_STOP) {
                    composableViewModel.onStop()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
        if (isFirstUploadMessages) {
            composableViewModel.uploadMessages()
        }
        val lazyMessageItems: LazyPagingItems<MessageModel> =
            composableViewModel.uploadMessagesForUser.collectAsLazyPagingItems()
        when (lazyMessageItems.itemCount) {
            0 -> Box {
                Text(text = "Nothing to show", style = TextStyle(fontSize = 22.sp))
            }
            else -> LazyColumn(reverseLayout= true,state = listState) {
                itemsIndexed(lazyMessageItems) { index, message ->
                    val next = try {
                        lazyMessageItems[index + 1]
                    } catch (e: IndexOutOfBoundsException){
                        null
                    }
                    //val isLastMessageFromAuthor = !(next == null || next.authorName != message?.authorName)
                    when (message) {
                        is TextMessageItem -> Box() {
                            TextMessage(msg = message)
                        }
                        is InfoMessageItem -> Box() {
                            InfoMessage(msg = message)
                        }
                        is DefaultMessageItem -> TODO()
                        is FileMessageItem -> TODO()
                        is GifMessageItem -> TODO()
                        is ImageMessageItem -> TODO()
                        is TransferMessageItem -> TODO()
                        is UnionMessageItem -> TODO()
                        null -> TODO()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun screenPrev() {

}
