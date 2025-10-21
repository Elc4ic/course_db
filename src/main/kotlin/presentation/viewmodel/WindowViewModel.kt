package presentation.viewmodel

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.logsPath
import presentation.components.DropdownSelector
import presentation.screens.HashTableScreen
import presentation.screens.LogScreen
import presentation.screens.OpenFile
import presentation.screens.ReportScreen
import presentation.screens.TreeDrawScreen
import presentation.screens.TreeScreen
import presentation.screens.TreeTextScreen

class WindowViewModel(val avm: AppViewModel) {

    val fileKey = "Выберете файл"
    val filePicker: Pair<String, @Composable (String) -> Unit> = fileKey to { OpenFile(it, this,false) }
    val map: Array<Pair<String, @Composable (String) -> Unit>> = arrayOf(
        "Книги" to { HashTableScreen(it, avm, this) },
        "Экземпляры1" to { TreeTextScreen(it, avm, this) },
        "Экземпляры2" to { TreeScreen(it, avm, this) },
        "Экземпляры3" to { TreeDrawScreen(it, avm, this) },
        "Отчет" to { ReportScreen(it, avm, this) },
        "Отладка" to { LogScreen(it, logsPath, this) })
    val windows = mutableStateMapOf<String, @Composable (String) -> Unit>("Выберете файл" to { OpenFile(it, this) })

    val selector: @Composable (String) -> Unit = { title ->
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            DropdownSelector(
                label = "Окно",
                items = getKeys(),
                selected = title,
                onSelect = { str ->
                    windows.remove(title)
                    windows += map.first { it.first == str }
                },
            )
            IconButton(onClick = {
                openWindow(getKeys().first { !windows.keys.contains(it) })
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }

    fun openWindow(key: String) {
        windows += map.first { it.first == key }
    }

    fun openFilePicker() {
        windows += filePicker
    }

    fun closeWindow(key: String) {
        windows.remove(key)
    }

    fun addToFiles() {

    }

    fun appUpdate() {
        avm.update()
    }

    fun getKeys(): List<String> {
        return map.map { it.first }
    }
}