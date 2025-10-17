package presentation.screens

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import entities.Book
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import presentation.components.*
import presentation.viewmodel.AppViewModel
import presentation.viewmodel.WindowViewModel

@Composable
fun HashTableScreen(key: String, vm: AppViewModel, wvm: WindowViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var searchBook by remember { mutableStateOf(Book("", "", "")) }
    val state = rememberLazyListState()
    var toastMessage by remember { mutableStateOf("") }
    var showToast by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ToolStrip(
                onAdd = { showAddDialog = true },
                onDelete = { showDeleteDialog = true },
                onSearch = {
                    coroutineScope.launch {
                        vm.searchBook()?.let {
                            searchBook = it
                            showSearchDialog = true
                        }
                    }
                },
                onSave = { vm.saveBooks() },
                searchBar = {
                    TextField(
                        value = vm.bookField.value,
                        onValueChange = { vm.bookField.value = it },
                        label = { Text("Введите ISBN") }
                    )
                },
                windowSelector = {wvm.selector(key)}
            )
        }
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            SelectionContainer {
                HeaderTableRow()
                LazyColumn(
                    state = state,
                    modifier = Modifier.padding(top = 32.dp)
                ) {
                    itemsIndexed(vm.rows) { i, entry ->
                        val ib = entry.value
                        if (ib != null) TableRow(i, ib.toString(), vm.books[ib], entry.status) {
                            vm.deleteBook(it.isbn, it.title, it.author)
                        }
                    }
//                    itemsIndexed(vm.rows) { index, entry ->
//                        val ib = entry.value
//                        if (ib == null) TableRow(index, "", null, null){}
//                        else TableRow(index, ib.toString(), vm.books[ib], entry.status) {
//                            vm.deleteBook(it.isbn, it.title, it.author)
//                        }
//                    }
                }
            }
            VerticalScrollbar(
                modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd),
                adapter = rememberScrollbarAdapter(
                    scrollState = state
                )
            )
        }
    }
    if (showAddDialog) {
        AddBookDialog(
            { showAddDialog = false },
            { showToast = true; toastMessage = "Книга добавлена" }, vm
        )
    }
    if (showDeleteDialog) {
        DeleteBookDialog({ showDeleteDialog = false }, vm)
    }
    if (showSearchDialog) {
        SearchBookDialog({ showSearchDialog = false }, searchBook)
    }
    if (showToast) {
        ToastDialog(toastMessage) { showToast = false }
        LaunchedEffect(Unit) { delay(1000); showToast = false }
    }
}
