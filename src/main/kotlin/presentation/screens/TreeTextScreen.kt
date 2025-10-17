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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import entities.Instance
import kotlinx.coroutines.delay
import presentation.components.*
import presentation.viewmodel.AppViewModel
import presentation.viewmodel.WindowViewModel

@Composable
fun TreeTextScreen(key: String, vm: AppViewModel, wvm: WindowViewModel) {
    val showAddDialog = remember { mutableStateOf(false) }
    val showDeleteDialog = remember { mutableStateOf(false) }
    val showSearchDialog = remember { mutableStateOf(false) }
    val listInstance = remember { mutableStateOf<List<Instance>>(emptyList()) }
    var toastMessage by remember { mutableStateOf("") }
    var showToast by remember { mutableStateOf(false) }

    val state = rememberLazyListState()

    Scaffold(
        topBar = {
            ToolStrip(
                windowSelector = { wvm.selector(key) },
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
    if (showAddDialog.value) {
        AddInstanceDialog(
            { showAddDialog.value = false },
            { showToast = true; toastMessage = "Экземпляр добавлен" },
            vm
        )
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