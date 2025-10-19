import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.flow.MutableSharedFlow
import presentation.screens.OpenFile
import presentation.style.AppTheme
import presentation.viewmodel.AppViewModel
import presentation.viewmodel.WindowViewModel


fun main() = application {
    val windowFocusRequestSharedFlow = remember { MutableSharedFlow<String>() }
    val avm = remember { AppViewModel() }
    val wvm = remember { WindowViewModel(avm) }

    AppTheme {
        val windows by remember { derivedStateOf { wvm.windows.toMap() } }

        for ((key, content) in windows) {
            key(key) {
                Window(
                    title = key,
                    onCloseRequest = { wvm.closeWindow(key) },
                ) {
                    MaterialTheme {
                        LaunchedEffect(key) {
                            windowFocusRequestSharedFlow.collect { target ->
                                if (target == key) window.toFront()
                            }
                        }
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colors.background
                        ) {
                            content(key)
                        }
                    }
                }
            }
        }
    }
}
