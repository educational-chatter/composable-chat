package my.zukoap.composablechat.presentation.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import my.zukoap.composablechat.R
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
    // hoisted for FAB onClick scrolling
    val listState: LazyListState = rememberLazyListState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val composableViewModel: ComposableChatViewModel by viewModel()


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
        },
        floatingActionButton = {
            if (listState.firstVisibleItemIndex > 10) { // firstVisibleItemIndex is the index of a message at the bottom of the screen
                FloatingActionButton(onClick = {
                    coroutineScope.launch {
                        if (listState.firstVisibleItemIndex < 30)
                            listState.animateScrollToItem(0) // scroll to bottom smoothly
                        else
                            listState.scrollToItem(0) // scroll to bottom fast
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_down),
                        contentDescription = "Scroll to bottom",
                        tint = MaterialTheme.colors.primaryVariant
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            UserInput(sendMessage = composableViewModel::sendMessage,
                resetScroll = {
                    coroutineScope.launch {
                        listState.scrollToItem(0) // scroll to end when sending message
                    }
                })
        }
    ) {
        // check for internet connection ( doesn't work properly, needs rework )
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

            // It is made to fully initialize viewmodel when there is a network, otherwise it crashes
            // It may cause problems
            // Also, according to google:  LaunchedEffect(true) is as suspicious as a while(true).
            // Even though there are valid use cases for it, always pause and make sure that's what you need.
            LaunchedEffect(true) {
                composableViewModel.initOnInternet()
            }

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

            // All livedata as state
            val internetConnectionState by composableViewModel.internetConnectionState.observeAsState()
            val displayableUIObject by composableViewModel.displayableUIObject.observeAsState()
            val countUnreadMessages by composableViewModel.countUnreadMessages.observeAsState()

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

            if (countUnreadMessages == null || countUnreadMessages!! <= 0) {
                Log.d("countUnreadMessages", "null or zero")
            } else {
                Log.d("countUnreadMessages", if (countUnreadMessages!! < 10) "<9" else "9+")
            }

            // This is needed to clear focus from textField and hide a keyboard
            // It's the simplest way i've found, it can be much better
            val keyboardController = LocalSoftwareKeyboardController.current
            val focusManager = LocalFocusManager.current
            val interactionSource = remember { MutableInteractionSource() }


            val lazyMessageItems: LazyPagingItems<MessageModel> =
                composableViewModel.uploadMessagesForUser.collectAsLazyPagingItems()

            // Loading screen when we have no messages in our viewmodel, else show LazyColumn with mapped messages
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
                else -> LazyColumn(
                    reverseLayout = true,
                    state = listState,
                    modifier = Modifier
                        .padding(
                            top = it.calculateTopPadding(),
                            bottom = it.calculateBottomPadding()
                        )
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null    // this gets rid of the ripple effect
                        ) {
                            keyboardController?.hide()
                            focusManager.clearFocus(true)
                        }
                ) {
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
                    // This is for debugging purposes
                    lazyMessageItems.apply {
                        when {
                            loadState.refresh is LoadState.Loading -> {
                                Log.d("LOADSTATE.REFRESH.LOAD", "Loading")
                            }
                            loadState.append is LoadState.Loading -> {
                                Log.d("LOADSTATE.APPEND.LOAD", "Loading")
                            }
                            loadState.refresh is LoadState.Error -> {
                                val e =
                                    lazyMessageItems.loadState.refresh as LoadState.Error
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
