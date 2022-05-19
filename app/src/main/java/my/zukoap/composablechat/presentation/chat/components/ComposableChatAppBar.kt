package my.zukoap.composablechat.presentation.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import my.zukoap.composablechat.R
import my.zukoap.composablechat.presentation.ui.theme.ComposableChatTheme

@Composable
fun ComposableChatAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = { },
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val backgroundColors =
        TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colors.surface)
    val backgroundColor = backgroundColors.containerColor(
        scrollFraction = scrollBehavior?.scrollFraction ?: 0f
    ).value
    val foregroundColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = Color.Transparent,
        scrolledContainerColor = Color.Transparent
    )
    Box(
        modifier = Modifier
            .background(backgroundColor)
    ) {
        CenterAlignedTopAppBar(
            modifier = modifier,
            actions = actions,
            title = title,
            scrollBehavior = scrollBehavior,
            colors = foregroundColors,
            navigationIcon = {
                ComposableChatAvatar(
                    contentDescription = stringResource(id = R.string.chat_avatar),
                    modifier = Modifier
                        .size(64.dp)
                        //.clickable(onClick = onNavIconPressed) // You can make it clickable()
                        .padding(16.dp)
                )
            }
        )
    }
}

@Preview
@Composable
fun ComposableChatAppBarPreview() {
    ComposableChatTheme {
        ComposableChatAppBar(title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Поддержка")
                Text(text = "", fontSize = 14.sp, color = MaterialTheme.colors.secondaryVariant)
            }
        })
    }
}

@Preview
@Composable
fun ComposableChatBarPreviewDark() {
    ComposableChatTheme(isDarkTheme = true) {
        ComposableChatAppBar(title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Поддержка")
                Text(text = "", fontSize = 14.sp, color = MaterialTheme.colors.secondaryVariant)
            }
        })
    }
}
