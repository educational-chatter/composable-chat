package my.zukoap.composablechat.presentation.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import my.zukoap.composablechat.R
import my.zukoap.composablechat.presentation.chat.model.InfoMessageItem
import my.zukoap.composablechat.presentation.chat.model.Role
import my.zukoap.composablechat.presentation.chat.model.TextMessageItem
import java.text.SimpleDateFormat

@Composable
fun TextMessage(
    msg: TextMessageItem,
    //isFirstMessageByAuthor: Boolean,
    //isLastMessageByAuthor: Boolean,
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
    ) {
        Column(modifier = Modifier.widthIn(0.dp, 360.dp).padding(horizontal = 12.dp, vertical = 4.dp)) {
            TextBubble(message = msg.message, isUserMe = msg.role == Role.USER)
            AuthorInfo(authorName = msg.authorName, timestamp = msg.timestamp, isUserMe = msg.role == Role.USER)
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


private val formatTime = SimpleDateFormat("HH:mm")
@Composable
private fun AuthorInfo(authorName: String, timestamp: Long, isUserMe: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 8.dp, top = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = if (isUserMe) Arrangement.End else Arrangement.Start) {
        if (!isUserMe) {
            Icon(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(32.dp)
                    .border(3.dp, MaterialTheme.colors.surface, CircleShape)
                    .clip(CircleShape),
                painter = painterResource(id = R.drawable.ic_operator),
                contentDescription = null,
                tint = MaterialTheme.colors.primary
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

private val ChatBubbleShape = RoundedCornerShape(8.dp, 8.dp, 8.dp, 8.dp)

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