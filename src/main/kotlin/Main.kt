import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.flow.MutableSharedFlow
import presentation.screens.OpenFile
import presentation.viewmodel.AppViewModel
import presentation.viewmodel.WindowViewModel


fun main() = application {

    val windowFocusRequestSharedFlow = remember { MutableSharedFlow<String>() }
    val avm = remember { AppViewModel() }
    val wvm = remember { WindowViewModel(avm) }
    wvm.windows.forEach { windowType ->
        key(windowType) {
            Window(
                title = windowType.key,
                onCloseRequest = {
                    wvm.closeWindow(windowType.key)
                },
            ) {
                LaunchedEffect(Unit) {
                    windowFocusRequestSharedFlow
                        .collect {
                            window.toFront()
                        }
                }
                windowType.value(windowType.key)
            }
        }
    }
}
