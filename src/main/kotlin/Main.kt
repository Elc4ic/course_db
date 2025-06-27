import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.flow.MutableSharedFlow
import presentation.screens.HashTableScreen
import presentation.screens.ReportScreen
import presentation.screens.TreeScreen
import presentation.viewmodel.AppViewModel


fun main() = application {

    val windowFocusRequestSharedFlow = remember { MutableSharedFlow<String>() }
    val vm = remember { AppViewModel() }

    val windows: Array<@Composable () -> Unit> = arrayOf(
        { HashTableScreen(vm) },
        { TreeScreen(vm) },
        { ReportScreen(vm) }
    )

    windows.forEach { windowType ->
        key(windowType) {
            Window(
                title = windowType.toString(),
                onCloseRequest = ::exitApplication,
            ) {
                LaunchedEffect(Unit) {
                    windowFocusRequestSharedFlow
                        .collect {
                            window.toFront()
                        }
                }
                windowType.invoke()
            }
        }
    }
}
