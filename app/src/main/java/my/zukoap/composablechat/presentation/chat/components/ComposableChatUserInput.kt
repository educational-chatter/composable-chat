package my.zukoap.composablechat.presentation.chat.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import my.zukoap.composablechat.R


@ExperimentalComposeUiApi
@Composable
fun UserInput(
    sendMessage: (message: String, repliedMessageId: String?) -> Unit,
    modifier: Modifier = Modifier,
    resetScroll: () -> Unit = {},
) {
    // Text inside text field
    var textState by remember { mutableStateOf(TextFieldValue()) }
    // Used to hide keyboard
    val keyboardController = LocalSoftwareKeyboardController.current

    // action on sending message event
    val onMessageSent = {
        sendMessage(textState.text, null)
        // Hide keyboard
        keyboardController?.hide()
        // Reset text field and close keyboard
        textState = TextFieldValue()
        // Move scroll to bottom
        resetScroll()
    }

    val a11ylabel = stringResource(id = R.string.textfield_desc)
    Surface(modifier = modifier) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
                .semantics {
                    contentDescription = a11ylabel
                },
            horizontalArrangement = Arrangement.End
        ) {
            UserInputText(
                textFieldValue = textState,
                onTextChanged = { textState = it }, // Awareness of user input
                modifier = Modifier
                    .padding(start = 16.dp, top = 10.dp, bottom = 10.dp)
                    .weight(1f),
                keyboardActions = KeyboardActions(onSend = {
                    onMessageSent()
                })
            )

            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 12.dp)
            ) {
                if (textState.text.isNotBlank()) {
                    SendButton {
                        onMessageSent()
                    }

                } else {
                    AttachFileButton {}
                }
            }
        }
    }
}

@Composable
private fun UserInputText(
    keyboardType: KeyboardType = KeyboardType.Text,
    onTextChanged: (TextFieldValue) -> Unit,
    textFieldValue: TextFieldValue,
    modifier: Modifier,
    keyboardActions: KeyboardActions
) {
    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { onTextChanged(it) },
        label = {
            Text(
                text = stringResource(id = R.string.textfield_hint),
                style = MaterialTheme.typography.body1
            )
        },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Send
        ),
        keyboardActions = keyboardActions,
        maxLines = 3,
        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current)
    )
}

@Composable
private fun SendButton(
    onMessageSent: () -> Unit,
) {
    // Send button
    IconButton(
        modifier = Modifier.size(36.dp),
        onClick = onMessageSent
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_send),
            contentDescription = "Send button",
            tint = MaterialTheme.colors.primaryVariant
        )
    }
}

@Composable
private fun AttachFileButton(
    onClick: () -> Unit,
) {
    IconButton(
        modifier = Modifier.size(36.dp),
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_attach_file),
            contentDescription = "Attach file button",
            tint = MaterialTheme.colors.primaryVariant
        )
    }
}