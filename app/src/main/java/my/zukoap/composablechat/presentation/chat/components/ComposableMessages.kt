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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import my.zukoap.composablechat.R
import my.zukoap.composablechat.common.ChatParams
import my.zukoap.composablechat.domain.entity.file.TypeDownloadProgress
import my.zukoap.composablechat.domain.entity.file.TypeFile
import my.zukoap.composablechat.domain.entity.message.MessageType
import my.zukoap.composablechat.presentation.chat.model.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat

private val chatBubbleShape = RoundedCornerShape(8.dp, 8.dp, 8.dp, 8.dp)
private val formatTime = SimpleDateFormat("HH:mm")

@Composable
fun TextMessage(
    message: TextMessageItem,
    onActionClick: (messageId: String, actionId: String) -> Unit,
) {
    MessageBubble(message = message, bubbleMaxWidth = 360.dp) {
        MessageSurface(role = message.role) {
            Text(
                text = message.message,
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier
                    .padding(10.dp)
            )
        }
        if (message.actions?.isNotEmpty() == true) {
            ActionsList(
                actions = message.actions,
                hasSelectedAction = message.hasSelectedAction,
                onItemClick = { actionId -> onActionClick(message.id, actionId) }
            )
        }
    }
}

@Composable
fun InfoMessage(
    message: InfoMessageItem,
) {
    Text(
        text = message.message,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = TextStyle(fontSize = 14.sp),
        color = MaterialTheme.colors.secondaryVariant,
    )
}

@Composable
fun ImageMessage(
    message: ImageMessageItem,
    updateData: (id: String, height: Int, width: Int) -> Unit,
) {
    MessageBubble(message = message, bubbleMaxWidth = 300.dp) {
        MessageSurface(role = message.role) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(message.image.url)
                    .crossfade(true)
                    .build(),
                contentDescription = message.image.name,
                onSuccess = { result ->
                    if (message.image.failLoading) {
                        updateData(
                            message.id,
                            result.result.drawable.intrinsicHeight,
                            result.result.drawable.intrinsicWidth
                        )
                    }
                },
                placeholder = painterResource(id = R.drawable.ic_baseline_image_24),
                contentScale = ContentScale.Inside,
                error = painterResource(R.drawable.ic_baseline_broken_image_24),
                modifier = Modifier
                    .padding(8.dp)
                    .background(roleColor(message.role))
                    .clip(chatBubbleShape)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun FileMessage(
    message: FileMessageItem,
    onFileClick: (id: String, documentName: String, documentUrl: String) -> Unit
) {
    MessageBubble(message = message) {
        MessageSurface(role = message.role) {
            FileInfo(file = message.document, messageId = message.id, onFileClick = onFileClick)
        }
    }
}

@Composable
private fun FileInfo(
    file: FileModel,
    messageId: String,
    onFileClick: (id: String, documentName: String, documentUrl: String) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(12.dp)
            .widthIn(0.dp, 160.dp), contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(start = 0.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)
                    .size(36.dp)
            ) {
                val iconsColor = MaterialTheme.colors.onSurface
                lateinit var painter: Painter
                var contentDescription = ""
                when (file.typeDownloadProgress) {
                    TypeDownloadProgress.NOT_DOWNLOADED -> {
                        painter = painterResource(id = R.drawable.ic_file_download)
                        contentDescription = "File is not downloaded"
                    }
                    TypeDownloadProgress.DOWNLOADING -> {
                        CircularProgressIndicator(progress = 0.75f)
                        painter = painterResource(id = R.drawable.ic_close)
                        contentDescription = "Downloading file"
                    }
                    TypeDownloadProgress.DOWNLOADED -> {
                        painter = painterResource(id = R.drawable.ic_file)
                        contentDescription = "File is downloaded"
                    }
                }
                Icon(
                    painter = painter,
                    contentDescription = contentDescription,
                    modifier = Modifier
                        .clickable { onFileClick(messageId, file.name, file.url) },
                    tint = iconsColor
                )
            }
            Column(modifier = Modifier.padding(end = 4.dp)) {
                Text(
                    text = file.name,
                    color = MaterialTheme.colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = file.sizeString(), color = MaterialTheme.colors.secondaryVariant)
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: MessageModel,
    bubbleMaxWidth: Dp = Dp.Unspecified,
    content: @Composable () -> Unit
) {
    RoleAlignedColumn(
        role = message.role,
        modifier = Modifier
            .widthIn(0.dp, bubbleMaxWidth)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        content()
        AuthorInfo(
            authorName = message.authorName,
            authorPreview = message.authorPreview,
            timestamp = message.timestamp,
            isUserMe = message.role == Role.USER,
            state = message.stateCheck
        )
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
    onItemClick: (actionId: String) -> Unit
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
                    onItemClick = { onItemClick(actionItem.id) }
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
        color = color,
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
private fun MessageSurface(
    role: Role,
    modifier: Modifier = Modifier,
    color: Color = roleColor(role = role),
    contentColor: Color = contentColorFor(color),
    border: BorderStroke? = null,
    elevation: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = chatBubbleShape,
        color = color,
        contentColor = contentColor,
        border = border,
        elevation = elevation
    ) {
        content()
    }
}

@Composable
inline fun RoleAlignedColumn(
    role: Role,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = when (role) {
            Role.USER -> {
                Alignment.End
            }
            Role.OPERATOR -> {
                Alignment.Start
            }
            else -> Alignment.CenterHorizontally
        }
    ) {
        content()
    }
}

@Composable
inline fun RoleAlignedBox(
    role: Role,
    modifier: Modifier = Modifier,
    propagateMinConstraints: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        contentAlignment = when (role) {
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
            .then(modifier),
        propagateMinConstraints = propagateMinConstraints
    ) {
        content()
    }
}

@Composable
private fun roleColor(role: Role): Color {
    return if (role == Role.USER) {
        MaterialTheme.colors.primary
    } else {
        MaterialTheme.colors.surface
    }
}

@Composable
private fun FileModel.sizeString(): String {
    if (this.size == null) return ""
    val df = DecimalFormat("#.##")
    val countByteInKByte = 1000L
    val countByteInMByte = 1000L * 1000L
    val countByteInGByte = 1000L * 1000L * 1000L
    return when (this.size) {
        in 0L until countByteInKByte -> "${this.size} ${stringResource(R.string.com_crafttalk_chat_file_size_byte)}"
        in countByteInKByte until countByteInMByte -> {
            val value = this.size.toDouble() / countByteInKByte
            "${
                (df.parse(df.format(value)).toDouble())
            } ${stringResource(R.string.com_crafttalk_chat_file_size_Kb)}"
        }
        in countByteInMByte until countByteInGByte -> {
            val value = this.size.toDouble() / countByteInMByte
            "${
                (df.parse(df.format(value)).toDouble())
            } ${stringResource(R.string.com_crafttalk_chat_file_size_Mb)}"
        }
        in countByteInGByte until countByteInGByte * 1000L -> {
            val value = this.size.toDouble() / countByteInGByte
            "${
                (df.parse(df.format(value)).toDouble())
            } ${stringResource(R.string.com_crafttalk_chat_file_size_Gb)}"
        }
        else -> ""
    }
}

@Preview(showBackground = true)
@Composable
fun FilePrevDownloading() {
    val file = FileModel("wow", "mark.pdf", 750000, type = TypeFile.FILE)
    file.typeDownloadProgress = TypeDownloadProgress.DOWNLOADING
    FileInfo(file = file, "145") { id, name, url -> }
}

@Preview(showBackground = true)
@Composable
fun FilePrevNotDownloaded() {
    val file = FileModel("wow", "mark.pdf", 750000, type = TypeFile.FILE)
    file.typeDownloadProgress = TypeDownloadProgress.NOT_DOWNLOADED
    FileInfo(file = file, "145") { id, name, url -> }
}
