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
import data.Errors
import data.letIfTrue
import entities.Book
import kotlinx.coroutines.launch
import presentation.components.*
import presentation.viewmodel.AppViewModel
import presentation.viewmodel.WindowViewModel

@Composable
fun HashTableScreen(key: String, vm: AppViewModel, wvm: WindowViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var searchBook by remember { mutableStateOf(Book("", "", "")) }
    val state = rememberLazyListState()

    ToastContainer { scope, toaster ->
        Scaffold(
            topBar = {
                ToolStrip(
                    onAdd = { showAddDialog = true },
                    onDelete = { showDeleteDialog = true },
                    onAddFile = { wvm.openFilePicker() },
                    onSearch = {
                        scope.launch {
                            val book = vm.searchBook(toaster)
                            if (book == null) toaster(Errors.NO_SUCH_BOOK, true)
                            else {
                                searchBook = book
                                showSearchDialog = true
                            }
                        }
                    },
                    onSave = {
                        vm.saveBooks(toaster).letIfTrue { toaster("Файл обновлен", false) }
                    },
                    searchBar = {
                        TextField(
                            value = vm.bookField.value,
                            onValueChange = { vm.bookField.value = it },
                            label = { Text("Введите ISBN") }
                        )
                    },
                    windowSelector = { wvm.selector(key) }
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
                                vm.deleteBook(it.isbn, it.title, it.author, toaster).letIfTrue { toaster("Книга удалена", false) }
                            }
                        }
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.fillMaxHeight().align(Alignment.CenterEnd),
                    adapter = rememberScrollbarAdapter(
                        scrollState = state
                    )
                )
            }
            if (showAddDialog) {
                AddBookDialog(
                    { showAddDialog = false },
                    { toaster("Книга добавлена", false) }, vm, toaster
                )
            }
            if (showDeleteDialog) {
                DeleteBookDialog(
                    { showDeleteDialog = false },
                    { toaster("Книга удалена", false) }, vm, toaster
                )
            }
            if (showSearchDialog) {
                SearchBookDialog(
                    { showSearchDialog = false },
                    { toaster("Книга найдена", false) }, searchBook
                )
            }
        }

    }

}
