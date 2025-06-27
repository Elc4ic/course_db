package presentation.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberWindowState
import entities.Instance
import presentation.components.AddInstanceDialog
import presentation.components.SearchInstanceDialog
import presentation.components.ToolStrip
import presentation.components.drawVisibleNodes
import presentation.components.layoutTree
import presentation.viewmodel.AppViewModel
import kotlin.math.max
import kotlin.math.min

@Composable
fun TreeScreen(vm: AppViewModel) {
    val showAddDialog = remember { mutableStateOf(false) }
    val showSearchDialog = remember { mutableStateOf(false) }
    val listInstance = remember { mutableStateOf<List<Instance>>(emptyList()) }

    var scale by remember { mutableStateOf(0.01f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val root by remember { mutableStateOf(vm.tree.nodes) }
    val layout = remember(root) { layoutTree(root).first }
    val textMeasurer = rememberTextMeasurer()

    Scaffold(
        topBar = {
            ToolStrip(
                title = "Экземпляры",
                onAdd = { showAddDialog.value = true },
                onDelete = { vm.deleteInstance() },
                onSearch = {
                    listInstance.value = vm.searchInstance()
                },
                onSave = { vm.saveInstances() },
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

        val windowState = rememberWindowState()
        val density = LocalDensity.current

        val visibleRect = remember(scale, offset, windowState.size) {
            with(density) {
                val width = windowState.size.width.toPx() * 1.5f
                val height = windowState.size.height.toPx() * 1.5f
                val off = Offset(width, height)
                Rect(
                    offset = (-offset - off) / scale,
                    size = Size(
                        2 * width / scale,
                        2 * height / scale
                    )
                )
            }
        }

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
                    .border(width = 2.dp, color = Color.Black)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            listInstance.value = vm.getInstances(layout?.clicked(offset))
                            showSearchDialog.value = true
                        }
                    }
            ) {
                layout?.let {
                    drawVisibleNodes(it, textMeasurer, visibleRect)
                }
            }
        }
    }
    if (showAddDialog.value) {
        AddInstanceDialog({ showAddDialog.value = false }, vm)
    }
    if (showSearchDialog.value) {
        SearchInstanceDialog({ showSearchDialog.value = false }, listInstance.value)
    }
}