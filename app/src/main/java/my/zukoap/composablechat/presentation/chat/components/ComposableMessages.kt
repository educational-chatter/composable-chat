package my.zukoap.composablechat.presentation.chat.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import my.zukoap.composablechat.R
import my.zukoap.composablechat.presentation.chat.model.ActionItem
import my.zukoap.composablechat.presentation.chat.model.InfoMessageItem
import my.zukoap.composablechat.presentation.chat.model.Role
import my.zukoap.composablechat.presentation.chat.model.TextMessageItem
import java.text.SimpleDateFormat

@Composable
fun TextMessage(
    msg: TextMessageItem,
    //isFirstMessageByAuthor: Boolean,
    //isLastMessageByAuthor: Boolean,
    onActionClick: (messageId: String, actionId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = when (msg.role) {
            Role.USER -> {
                Alignment.CenterEnd
            }
            Role.OPERATOR -> {
                Alignment.CenterStart
            }
            else -> {
                Alignment.Center
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        Column(
            modifier = Modifier
                .widthIn(0.dp, 360.dp)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            TextBubble(message = msg.message, isUserMe = msg.role == Role.USER)
            if (msg.actions?.isNotEmpty() == true) {
                ActionsList(
                    actions = msg.actions,
                    hasSelectedAction = msg.hasSelectedAction,
                    onitemClick = { actionId -> onActionClick(msg.id, actionId) }
                )
            }
            AuthorInfo(
                authorName = msg.authorName,
                authorPreview = msg.authorPreview,
                timestamp = msg.timestamp,
                isUserMe = msg.role == Role.USER
            )
        }
    }
}

@Composable
fun InfoMessage(
    msg: InfoMessageItem,
    modifier: Modifier = Modifier
) {
    Text(
        text = msg.message,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .then(modifier),
        textAlign = TextAlign.Center,
        style = TextStyle(fontSize = 14.sp),
        color = MaterialTheme.colors.secondaryVariant,
    )
}


private val ChatBubbleShape = RoundedCornerShape(8.dp, 8.dp, 8.dp, 8.dp)

@Composable
private fun ActionsList(
    actions: List<ActionItem>,
    hasSelectedAction: Boolean,
    onitemClick: (actionId: String) -> Unit
) {
    Surface(
        modifier = Modifier.padding(top = 8.dp),
        shape = ChatBubbleShape,
        border = BorderStroke(2.dp, MaterialTheme.colors.secondaryVariant)
    ) {
        Column {
            actions.forEach { actionItem ->
                ActionSurface(
                    actionItem = actionItem,
                    hasSelectedAction = hasSelectedAction,
                    onitemClick = { onitemClick(actionItem.id) }
                )
                Divider(color = MaterialTheme.colors.secondaryVariant, thickness = 2.dp)
            }
        }
    }
}


@Composable
private fun ActionSurface(
    actionItem: ActionItem,
    hasSelectedAction: Boolean,
    onitemClick: () -> Unit = {}
) {
    val color =
        if (actionItem.isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.secondary
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !hasSelectedAction) { onitemClick() },
        color = if (actionItem.isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.secondary,
        contentColor = contentColorFor(backgroundColor = color),
    ) {
        Text(
            text = actionItem.actionText,
            modifier = Modifier
                .padding(10.dp),
            textAlign = TextAlign.Center
        )
    }
}

private val formatTime = SimpleDateFormat("HH:mm")

@Composable
private fun AuthorInfo(authorName: String, authorPreview: String?, timestamp: Long, isUserMe: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isUserMe) Arrangement.End else Arrangement.Start
    ) {
        if (!isUserMe) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(authorPreview ?: R.drawable.ic_operator)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_operator),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier =  Modifier
                    .padding(end = 8.dp)
                    .size(32.dp)
                    .border(3.dp, MaterialTheme.colors.surface, CircleShape)
                    .clip(CircleShape)
            )
        }
        Text(
            text = authorName,
            style = TextStyle(fontSize = 12.sp),
            color = MaterialTheme.colors.secondaryVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = formatTime.format(timestamp),
            style = TextStyle(fontSize = 12.sp),
            color = MaterialTheme.colors.secondaryVariant
        )
    }
}

@Composable
fun TextBubble(
    message: String,
    isUserMe: Boolean,
) {
    val backgroundBubbleColor = if (isUserMe) {
        MaterialTheme.colors.primary
    } else {
        MaterialTheme.colors.surface
    }
    Column {
        Surface(
            color = backgroundBubbleColor,
            shape = ChatBubbleShape,
        ) {
            Text(
                text = message,
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            )
        }
    }
}