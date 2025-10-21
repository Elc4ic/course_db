package presentation.screens


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.Errors
import entities.Instance
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import presentation.components.*
import presentation.viewmodel.AppViewModel
import presentation.viewmodel.WindowViewModel

@Composable
fun TreeTextScreen(key: String, vm: AppViewModel, wvm: WindowViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    val listInstance = remember { mutableStateOf<List<Instance>>(emptyList()) }

    val state = rememberLazyListState()

    val toast = rememberToastState()
    val scope = rememberCoroutineScope()

    ToastContainer { scope, toast, toaster ->
        Scaffold(
            topBar = {
                ToolStrip(
                    windowSelector = { wvm.selector(key) },
                    onAdd = { showAddDialog = true },
                    onAddFile = { wvm.openFilePicker() },
                    onDelete = { showDeleteDialog = true },
                    onSearch = {
                        vm.searchInstance(scope, toast)?.let {
                            if (it.isEmpty()) toaster(Errors.NO_SUCH_INSTANCES, true)
                            else {
                                listInstance.value = it
                                showSearchDialog = true
                            }
                        }

                    },
                    onSave = { vm.saveInstances(scope, toast)?.let { if (it) toaster("Файл обновлен", false) } },
                    searchBar = {
                        TextField(
                            value = vm.instanceField.value,
                            onValueChange = { vm.instanceField.value = it },
                            label = { Text("Введите ISBN") }
                        )
                    }
                )
            },
        ) { padding ->
            LazyColumn(
                state = state,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                items(vm.treeList) { line ->
                    Text(
                        text = line,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
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