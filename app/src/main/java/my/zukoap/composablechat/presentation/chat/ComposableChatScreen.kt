package my.zukoap.composablechat.presentation.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import kotlinx.coroutines.ExperimentalCoroutinesApi
import my.zukoap.composablechat.domain.entity.auth.Visitor
import my.zukoap.composablechat.domain.entity.internet.InternetConnectionState
import my.zukoap.composablechat.presentation.DisplayableUIObject
import my.zukoap.composablechat.presentation.chat.components.*
import my.zukoap.composablechat.presentation.chat.model.*
import my.zukoap.composablechat.presentation.helper.network.ConnectionState
import my.zukoap.composablechat.presentation.helper.network.connectivityState
import org.koin.androidx.compose.viewModel

var topAppBarMainText = "Поддержка"
var topAppBarHelpText = mutableStateOf("")

@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
@Composable
fun ComposableChatScreen(
    visitor: Visitor? = null,
    isFirstLaunch: MutableState<Boolean>
) {
    Scaffold(
        topBar = {
            ComposableChatAppBar(title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = topAppBarMainText, fontSize = 20.sp)
                    Text(
                        text = topAppBarHelpText.value,
                        fontSize = 14.sp,
                        color = MaterialTheme.colors.secondaryVariant
                    )
                }
            })
        }
    ) {
        val connection by connectivityState()
        val isConnected = connection === ConnectionState.Available
        var pressed by remember { mutableStateOf(true) }
        if (isFirstLaunch.value) {
            isFirstLaunch.value = if (!isConnected) {
                pressed = ErrorScreen {}
                if (!pressed) {
                    return@Scaffold
                } else {
                    if (!isConnected) {
                        return@Scaffold
                    }
                    false
                }
            } else {
                false
            }
        } else {

            val composableViewModel: ComposableChatViewModel by viewModel()
            val listState: LazyListState = rememberLazyListState()
            val lifecycleOwner = LocalLifecycleOwner.current

            /*TODO add observers*/

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

            val internetConnectionState by composableViewModel.internetConnectionState.observeAsState()
            when (internetConnectionState) {
                InternetConnectionState.NO_INTERNET -> {
                    topAppBarHelpText.value = "Соединение потеряно"
                    Log.d("InternetConnectionState", "NO_INTERNET")
                }
                InternetConnectionState.RECONNECT -> {
                    topAppBarHelpText.value = "Восстанавливаем соединение"
                    Log.d("InternetConnectionState", "RECONNECT")
                }
                InternetConnectionState.HAS_INTERNET -> {
                    //topAppBarHelpText.value = "онлайн"
                    Log.d("InternetConnectionState", "INTERNET")
                }
                else -> {}
            }

            val displayableUIObject by composableViewModel.displayableUIObject.observeAsState()
            when (displayableUIObject) {
                DisplayableUIObject.NOTHING -> {
                    Log.d("displayableUIObject", "NOTHING")
                }
                DisplayableUIObject.SYNCHRONIZATION -> {
                    topAppBarHelpText.value = "Синхронизация"
                    Log.d("displayableUIObject", "SYNCHRONIZATION")
                }
                DisplayableUIObject.CHAT -> {
                    topAppBarMainText = "Поддержка"
                    if (internetConnectionState != InternetConnectionState.NO_INTERNET) topAppBarHelpText.value =
                        ""
                    Log.d("displayableUIObject", "CHAT")
                }
                DisplayableUIObject.FORM_AUTH -> {
                    Log.d("displayableUIObject", "FORM_AUTH")
                }
                DisplayableUIObject.WARNING -> {
                    Log.d("displayableUIObject", "WARNING")
                }
                DisplayableUIObject.OPERATOR_START_WRITE_MESSAGE -> {
                    Log.d("displayableUIObject", "OPERATOR_START_WRITE_MESSAGE")
                }
                DisplayableUIObject.OPERATOR_STOP_WRITE_MESSAGE -> {
                    Log.d("displayableUIObject", "OPERATOR_STOP_WRITE_MESSAGE")
                }
                else -> {}
            }

            val countUnreadMessages by composableViewModel.countUnreadMessages.observeAsState()
            if (countUnreadMessages == null || countUnreadMessages!! <= 0) {
                Log.d("countUnreadMessages", "null or zero")
            } else {
                Log.d("countUnreadMessages", if (countUnreadMessages!! < 10) "<9" else "9+")
            }

            val lazyMessageItems: LazyPagingItems<MessageModel> =
                composableViewModel.uploadMessagesForUser.collectAsLazyPagingItems()
            when (lazyMessageItems.itemCount) {
                0 -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colors.background,
                                shape = RoundedCornerShape(15.dp)
                            )
                            .fillMaxSize(0.2f), strokeWidth = 14.dp
                    )
                }
                else -> LazyColumn(reverseLayout = true, state = listState) {
                    items(items = lazyMessageItems) { message ->
                        if (message != null) {
                            RoleAlignedBox(message.role) {
                                when (message) {
                                    is TextMessageItem -> TextMessage(
                                        message = message,
                                        onActionClick = composableViewModel::selectAction
                                    )
                                    is InfoMessageItem -> InfoMessage(message = message)
                                    is FileMessageItem -> FileMessage(
                                        message = message,
                                        onFileClick = composableViewModel::downloadOrOpenDocument
                                    )
                                    is GifMessageItem -> GifMessage(
                                        message = message,
                                        updateData = composableViewModel::updateData
                                    )
                                    is ImageMessageItem -> ImageMessage(
                                        message = message,
                                        updateData = composableViewModel::updateData
                                    )
                                    is TransferMessageItem -> TransferMessage(message = message)
                                    is UnionMessageItem -> {}
                                    is SeparateItem -> DateText(timestamp = message.timestamp)
                                    else -> {} // DefaultMessageItem is just an empty container (LinearLayout)
                                }
                            }
                        }
                    }
                    lazyMessageItems.apply {
                        //Log.d("LOADSTATE", "${loadState}, items: $itemCount")
                        when {
                            loadState.refresh is LoadState.Loading -> {
                                Log.d("LOADSTATE.REFRESH.LOAD", "Loading")
                            }
                            loadState.append is LoadState.Loading -> {
                                Log.d("LOADSTATE.APPEND.LOAD", "Loading")
                            }
                            loadState.refresh is LoadState.Error -> {
                                val e = lazyMessageItems.loadState.refresh as LoadState.Error
                                Log.d("LOADSTATE.REFRESH.ERROR", e.error.localizedMessage!!)
                            }
                            loadState.append is LoadState.Error -> {
                                val e = lazyMessageItems.loadState.append as LoadState.Error
                                Log.d("LOADSTATE.APPEND.ERROR", e.error.localizedMessage!!)
                            }
                        }
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
