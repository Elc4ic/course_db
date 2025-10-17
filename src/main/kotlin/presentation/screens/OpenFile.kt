package presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.FileUtils
import data.logsPath
import presentation.viewmodel.AppViewModel
import presentation.viewmodel.WindowViewModel
import java.awt.FileDialog
import java.awt.Frame

@Composable
fun OpenFile(key: String, wvm: WindowViewModel) {
    var selectedBookStorage by remember { mutableStateOf(FileUtils.getPath(FileUtils.actualBooksStore)) }
    var selectedInstanceStorage by remember { mutableStateOf(FileUtils.getPath(FileUtils.actualInstanceStore)) }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Выберите файлы:", modifier = Modifier.padding(8.dp))
        Text(selectedBookStorage ?: "-", modifier = Modifier.padding(8.dp))
        Button(onClick = {
            val dialog = FileDialog(null as Frame?, "Select a File")
            dialog.isVisible = true
            if (dialog.file != null && dialog.file.endsWith(".txt")) {
                selectedBookStorage = "${dialog.directory}${dialog.file}"
            }
        }) {
            Text("Найти")
        }

        Text(selectedInstanceStorage ?: "-", modifier = Modifier.padding(8.dp))
        Button(onClick = {
            val dialog = FileDialog(null as Frame?, "Select a File")
            dialog.isVisible = true
            if (dialog.file != null && dialog.file.endsWith(".txt") && !dialog.file.endsWith(logsPath)) {
                selectedInstanceStorage = "${dialog.directory}${dialog.file}"
            }
        }) {
            Text("Найти")
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            Button(onClick = {
                if (selectedInstanceStorage != null && selectedBookStorage != null) {
                    FileUtils.setPaths(selectedBookStorage.toString(), selectedInstanceStorage.toString())
                    wvm.appUpdate()
                    wvm.openWindow(wvm.getKeys().first())
                    wvm.closeWindow(key)
                }
            }) {
                Text("Ввод")
            }
        }

    }

}