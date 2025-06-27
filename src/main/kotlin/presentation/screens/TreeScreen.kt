package presentation.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import presentation.components.TreeRow
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
    val root by remember { mutableStateOf(vm.tree.nodes) }

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
    ) {

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            TreeRow(node = root, onSearch = {
                listInstance.value = vm.getInstances(it)
                showSearchDialog.value = true
            })
        }

    }
    if (showAddDialog.value) {
        AddInstanceDialog({ showAddDialog.value = false }, vm)
    }
    if (showSearchDialog.value) {
        SearchInstanceDialog({ showSearchDialog.value = false }, listInstance.value)
    }
}