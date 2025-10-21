package presentation.screens


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import entities.Instance
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import presentation.components.*
import presentation.viewmodel.AppViewModel
import presentation.viewmodel.WindowViewModel
import kotlin.math.max
import kotlin.math.min

@Composable
fun TreeDrawScreen(key: String, vm: AppViewModel, wvm: WindowViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    val listInstance = remember { mutableStateOf<List<Instance>>(emptyList()) }

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val root by remember { mutableStateOf(vm.tree.nodes) }
    val layout = remember(root) { layoutTree(root).first }
    val textMeasurer = rememberTextMeasurer()

    ToastContainer { scope, toast, toaster ->
        Scaffold(
            topBar = {
                ToolStrip(
                    windowSelector = { wvm.selector(key) },
                    onAdd = { showAddDialog = true },
                    onAddFile = { wvm.openFilePicker() },
                    onDelete = { showDeleteDialog = true },
                    onSearch = {
                        vm.searchInstance(scope, toast)?.let { listInstance.value = it }
                    },
                    onSave = { vm.saveInstances(scope, toast) },
                    searchBar = {
                        TextField(
                            value = vm.instanceField.value,
                            onValueChange = { vm.instanceField.value = it },
                            label = { Text("Введите ISBN") }
                        )
                    }
                )
            },
            floatingActionButton = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FloatingActionButton(onClick = { scale = min(scale * 1.2f, 3f) }) {
                        Icon(Icons.Default.Add, contentDescription = "zoom+")
                    }
                    FloatingActionButton(onClick = {
                        offset = -Offset(layout?.x ?: 0f, layout?.y ?: 0f) * scale
                    }) {
                        Icon(Icons.Default.Home, contentDescription = "go in root")
                    }
                    FloatingActionButton(onClick = { scale = max(scale * 0.8f, 0.0001f) }) {
                        Icon(Icons.Default.Close, contentDescription = "zoom-")
                    }
                    Text("$offset")
                }
            }
        ) {

            val transformModifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.0001f, 5f)
                        offset += pan
                    }
                }

            Box(modifier = transformModifier) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offset.x
                            translationY = offset.y
                        }
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                vm.getInstances(layout?.clicked(offset))?.let {
                                    listInstance.value = it
                                    showSearchDialog = true
                                }
                            }
                        }
                ) {
                    layout?.let {
                        drawVisibleNodes(it, textMeasurer)
                    }
                }
            }
        }
        if (showAddDialog) {
            AddInstanceDialog(
                { showAddDialog = false },
                { toaster("Экземпляр добавлен", false) }, vm, scope, toast
            )
        }
        if (showDeleteDialog) {
            DeleteInstanceDialog(
                { showDeleteDialog = false },
                { toaster("Экземпляр удален", false) }, vm, scope, toast
            )
        }
        if (showSearchDialog) {
            SearchInstanceDialog(
                { showSearchDialog = false },
                { toaster("Экземпляры найдены", false) }, listInstance.value
            )
        }
    }
}