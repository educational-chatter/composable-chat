package my.zukoap.composablechat.presentation.chat.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import coil.request.ImageRequest
import my.zukoap.composablechat.R
import my.zukoap.composablechat.common.ChatParams
import my.zukoap.composablechat.domain.entity.message.MessageType
import my.zukoap.composablechat.presentation.chat.model.*
import java.text.SimpleDateFormat

private val chatBubbleShape = RoundedCornerShape(8.dp, 8.dp, 8.dp, 8.dp)
private val formatTime = SimpleDateFormat("HH:mm", ChatParams.locale)

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
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalAlignment = when (msg.role) {
                Role.USER -> {
                    Alignment.End
                }
                Role.OPERATOR -> {
                    Alignment.Start
                }
                else -> Alignment.CenterHorizontally
            }
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
                isUserMe = msg.role == Role.USER,
                state = msg.stateCheck
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

@Composable
fun ImageMessage(
    msg: ImageMessageItem,
    updateData: (id: String, height: Int, width: Int) -> Unit,
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
                .widthIn(0.dp, 250.dp)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalAlignment = when (msg.role) {
                Role.USER -> {
                    Alignment.End
                }
                Role.OPERATOR -> {
                    Alignment.Start
                }
                else -> Alignment.CenterHorizontally
            }
        ) {
            val backgroundBubbleColor = if (msg.role == Role.USER) {
                MaterialTheme.colors.primary
            } else {
                MaterialTheme.colors.surface
            }
            Surface(shape = chatBubbleShape, color = backgroundBubbleColor) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(msg.image.url)
                        .crossfade(true)
                        .build(),
                    contentDescription = msg.image.name,
                    onSuccess = { result ->
                        if (msg.image.failLoading) {
                            updateData(
                                msg.id,
                                result.result.drawable.intrinsicHeight,
                                result.result.drawable.intrinsicWidth
                            )
                        }
                    },
                    placeholder = painterResource(id = R.drawable.ic_baseline_image_24),
                    contentScale = ContentScale.FillBounds,
                    error = painterResource(R.drawable.ic_baseline_broken_image_24),
                    modifier = Modifier
                        .padding(8.dp)
                        .background(backgroundBubbleColor)
                        .clip(chatBubbleShape)
                        .fillMaxWidth()
                )
            }
            AuthorInfo(
                authorName = msg.authorName,
                authorPreview = msg.authorPreview,
                timestamp = msg.timestamp,
                isUserMe = msg.role == Role.USER,
                state = msg.stateCheck
            )
        }
    }
}


@Composable
fun DateText(timestamp: Long) {
    val formatYear = SimpleDateFormat("yyyy", ChatParams.locale)
    val formatTime = SimpleDateFormat("dd MMMM", ChatParams.locale)

    val nowYear = formatYear.format(System.currentTimeMillis())
    val currentYear = formatYear.format(timestamp)
    val date = formatTime.format(timestamp)

    val text = if (nowYear == currentYear) {
        date
    } else {
        "$date $currentYear"
    }
    Text(
        text = text,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = TextStyle(fontSize = 14.sp),
        color = MaterialTheme.colors.secondaryVariant,
    )
}

@Composable
private fun ActionsList(
    actions: List<ActionItem>,
    hasSelectedAction: Boolean,
    onitemClick: (actionId: String) -> Unit
) {
    Surface(
        modifier = Modifier.padding(top = 8.dp),
        shape = chatBubbleShape,
        border = BorderStroke(2.dp, MaterialTheme.colors.secondaryVariant)
    ) {
        Column {
            actions.forEach { actionItem ->
                ActionSurface(
                    actionItem = actionItem,
                    hasSelectedAction = hasSelectedAction,
                    onItemClick = { onitemClick(actionItem.id) }
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
    onItemClick: () -> Unit = {}
) {
    val color =
        if (actionItem.isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.secondary
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !hasSelectedAction) { onItemClick() },
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

@Composable
private fun AuthorInfo(
    authorName: String,
    authorPreview: String?,
    timestamp: Long,
    isUserMe: Boolean,
    state: MessageType
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isUserMe) Arrangement.End else Arrangement.Start
    ) {
        if (!isUserMe) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(authorPreview ?: R.drawable.ic_operator)
                    .crossfade(true)
                    .error(R.drawable.ic_baseline_broken_image_24)
                    .build(),
                placeholder = painterResource(R.drawable.ic_operator),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
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
        if (isUserMe) {
/*            when (state) {
                MessageType.RECEIVED_BY_MEDIATO -> Icon(
                    modifier = Modifier,
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = null,
                    tint = contentColorFor(backgroundColor = MaterialTheme.colors.background)
                )
                MessageType.RECEIVED_BY_OPERATOR -> Icon(
                    modifier = Modifier,
                    painter = painterResource(id = R.drawable.ic_double_check),
                    contentDescription = null,
                    tint = contentColorFor(backgroundColor = MaterialTheme.colors.background)
                )
                else -> {}
            }*/
            Icon(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(16.dp),
                painter = painterResource(
                    id = when (state) {
                        MessageType.RECEIVED_BY_MEDIATO -> R.drawable.ic_check
                        MessageType.RECEIVED_BY_OPERATOR -> R.drawable.ic_double_check
                        else -> 0 // may cause problems, idk. But it is shorter than the case above
                    }
                ),
                contentDescription = null,
                tint = contentColorFor(backgroundColor = MaterialTheme.colors.background)
            )
        }
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
    Surface(
        color = backgroundBubbleColor,
        shape = chatBubbleShape
    ) {
        Text(
            text = message,
            style = TextStyle(fontSize = 16.sp),
            modifier = Modifier
                .padding(10.dp)
        )
    }
}