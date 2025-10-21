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
import kotlinx.coroutines.launch
import presentation.components.AddInstanceDialog
import presentation.components.DeleteInstanceDialog
import presentation.components.SearchInstanceDialog
import presentation.components.ToastContainer
import presentation.components.ToolStrip
import presentation.components.TreeRow
import presentation.components.rememberToastState
import presentation.viewmodel.AppViewModel
import presentation.viewmodel.WindowViewModel

@Composable
fun TreeScreen(key: String, vm: AppViewModel, wvm: WindowViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    val listInstance = remember { mutableStateOf<List<Instance>>(emptyList()) }
    val root by remember { mutableStateOf(vm.tree.nodes) }

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
        ) {

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TreeRow(node = root, onSearch = {
                    vm.getInstances(it)?.let { list -> listInstance.value = list }
                    if (!listInstance.value.isEmpty()) {
                        showSearchDialog = true
                    }
                })
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
                { toaster("Экземпляры найдены", false) },
                listInstance.value,
                { instance ->
                    vm.deleteInstance(instance.isbn, instance.inventoryNumber, scope, toast)?.let {
                        scope.launch { toaster("Экземпляр удален", false) }
                    }
                }
            )
        }
    }
}