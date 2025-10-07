package presentation.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import data.logsPath
import presentation.screens.HashTableScreen
import presentation.screens.LogScreen
import presentation.screens.OpenFile
import presentation.screens.ReportScreen
import presentation.screens.TreeTextScreen

class WindowViewModel(avm: AppViewModel) {
    val windows = mutableStateOf<Map<String, @Composable () -> Unit>>(mapOf("Файл" to { OpenFile(avm, this) }))

    fun openChooseFiles(vm: AppViewModel) {
        windows.value = mapOf("Файл" to { OpenFile(vm, this) })
    }

    fun openAfterLoad(vm: AppViewModel) {
        windows.value = mapOf(
            "Книги" to { HashTableScreen(vm) },
            "Экземпляры" to { TreeTextScreen(vm) },
            "Отчет" to { ReportScreen(vm) },
            "Отладка" to { LogScreen(logsPath) }
        )
        vm.update()
    }
}