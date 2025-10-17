package presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import entities.Instance
import kotlinx.coroutines.delay
import presentation.components.AddInstanceDialog
import presentation.components.DeleteInstanceDialog
import presentation.components.SearchInstanceDialog
import presentation.components.ToastDialog
import presentation.components.ToolStrip
import presentation.components.TreeRow
import presentation.viewmodel.AppViewModel
import presentation.viewmodel.WindowViewModel

@Composable
fun TreeScreen(key: String,vm: AppViewModel,wvm: WindowViewModel) {
    val showAddDialog = remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    val showSearchDialog = remember { mutableStateOf(false) }
    val listInstance = remember { mutableStateOf<List<Instance>>(emptyList()) }
    val root by remember { mutableStateOf(vm.tree.nodes) }
    var toastMessage by remember { mutableStateOf("") }
    var showToast by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ToolStrip(
                windowSelector = {wvm.selector(key)},
                onAdd = { showAddDialog.value = true },
                onDelete = { showDeleteDialog.value = true },
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
                if (!listInstance.value.isEmpty()) {
                    showSearchDialog.value = true
                }
            })
        }

    }
    if (showAddDialog.value) {
        AddInstanceDialog({ showAddDialog.value = false }, { showToast = true; toastMessage = "Экземпляр добавлен" }, vm)
    }
    if (showDeleteDialog.value) {
        DeleteInstanceDialog({ showDeleteDialog.value = false }, vm)
    }
    if (showSearchDialog.value) {
        SearchInstanceDialog({ showSearchDialog.value = false }, listInstance.value)
    }
    if (showToast) {
        ToastDialog(toastMessage) { showToast = false }
        LaunchedEffect(Unit) { delay(1000); showToast = false }
    }
}