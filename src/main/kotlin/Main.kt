import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.flow.MutableSharedFlow
import presentation.screens.HashTableScreen
import presentation.screens.LogScreen
import presentation.screens.ReportScreen
import presentation.screens.TreeDrawScreen
import presentation.screens.TreeScreen
import presentation.screens.TreeTextScreen
import presentation.viewmodel.AppViewModel


fun main() = application {

    val windowFocusRequestSharedFlow = remember { MutableSharedFlow<String>() }
    val vm = remember { AppViewModel() }

    val windows: Map<String, @Composable () -> Unit> = mapOf(
        "Книги" to { HashTableScreen(vm) },
        "Экземпляры" to { TreeTextScreen(vm) },
        "Отчет" to { ReportScreen(vm) },
        "Отладка" to { LogScreen("src/main/resources/logs.txt") }
    )

    windows.forEach { windowType ->
        key(windowType) {
            Window(
                title = windowType.key,
                onCloseRequest = ::exitApplication,
            ) {
                LaunchedEffect(Unit) {
                    windowFocusRequestSharedFlow
                        .collect {
                            window.toFront()
                        }
                }
                windowType.value.invoke()
            }
        }
    }
}
