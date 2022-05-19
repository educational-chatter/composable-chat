package my.zukoap.composablechat.presentation.chat.components

import android.view.MotionEvent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import my.zukoap.composablechat.R

@ExperimentalComposeUiApi
@Composable
fun ErrorScreen(onButtonClick: () -> Unit): Boolean{
    var pressed by remember { mutableStateOf(false) }
    Box(modifier = Modifier
        .fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.widthIn(0.dp, 300.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.ic_warning),
                contentDescription = "Warning sign",
                modifier = Modifier
                    .padding(16.dp)
                    .size(80.dp),
                tint = Color.Red
            )
            Text(text = stringResource(id = R.string.warning_title), fontSize = 24.sp, color = MaterialTheme.colors.secondaryVariant, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = stringResource(id = R.string.warning_reload), fontSize = 20.sp,color = MaterialTheme.colors.secondaryVariant, textAlign = TextAlign.Center)
            IconButton(onClick = onButtonClick,
                modifier = Modifier.then( Modifier.pointerInteropFilter {
                pressed = when (it.action) {
                    MotionEvent.ACTION_DOWN -> true
                    else -> false
                }
                true
            })) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_refresh),
                    contentDescription = "Warning sign",
                    modifier = Modifier
                        .padding(16.dp)
                        .size(60.dp)
                )
            }
        }
    }
    return pressed
}