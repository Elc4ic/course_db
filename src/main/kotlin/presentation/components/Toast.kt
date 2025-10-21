package presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ToastHost(toastState: ToastState) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = toastState.isVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                Modifier
                    .padding(16.dp)
                    .background(if (toastState.isError) Color.Red else Color(0xFF527B51), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(toastState.message, color = Color.White)
            }
        }
    }
}

class ToastState {
    var message by mutableStateOf("")
    var isVisible by mutableStateOf(false)
    var isError by mutableStateOf(false)

    suspend fun success(message: String, durationMillis: Long = 1000) {
        this.message = message
        isVisible = true
        delay(durationMillis)
        isVisible = false
    }

    suspend fun error(message: String, durationMillis: Long = 5000) {
        this.message = message
        isVisible = true
        isError = true
        delay(durationMillis)
        isVisible = false
        isError = false
    }
}

@Composable
fun rememberToastState() = remember { ToastState() }

var toastTemplate: (CoroutineScope, ToastState) -> (String, Boolean) -> Unit = { scope, toast ->
    { msg, err ->
        if (!err)
            scope.launch { toast.success(msg) }
        else
            scope.launch { toast.error(msg) }
    }
}

@Composable
fun ToastContainer(
    scope: CoroutineScope = rememberCoroutineScope(),
    toast: ToastState = rememberToastState(),
    content: @Composable (CoroutineScope, ToastState, (String, Boolean) -> Unit) -> Unit
) {
    content(scope, toast, toastTemplate(scope, toast))
    ToastHost(toast)
}
