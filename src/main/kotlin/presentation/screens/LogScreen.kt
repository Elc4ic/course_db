package presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import presentation.viewmodel.WindowViewModel
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds

@Composable
fun LogScreen(key: String,filePath: String,wvm: WindowViewModel) {
    val file = remember(filePath) { File(filePath) }
    var lines by remember { mutableStateOf(listOf<String>()) }
    val scope = rememberCoroutineScope()
    val state = rememberLazyListState()

    fun readFile() {
        if (file.exists()) {
            lines = file.readLines()
        }
    }

    LaunchedEffect(filePath) {
        readFile()
        val watchService = FileSystems.getDefault().newWatchService()
        val dir = file.toPath().parent
        dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)
        state.scrollToItem(lines.size)
        scope.launch(Dispatchers.IO) {
            while (true) {
                val key = watchService.take()
                for (event in key.pollEvents()) {
                    val changed = event.context() as? Path
                    if (changed != null && changed.endsWith(file.name)) {
                        readFile()
                    }
                }
                key.reset()
            }
        }
    }

    LaunchedEffect(lines.size) {
        if (lines.isNotEmpty()) {
            state.animateScrollToItem(lines.lastIndex)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { wvm.selector(key) }) }
    ) { padding ->
        LazyColumn(
            state = state,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(lines) { line ->
                Text(
                    text = line,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        }
    }
}