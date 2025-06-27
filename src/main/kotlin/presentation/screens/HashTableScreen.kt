package presentation.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import entities.Book
import kotlinx.coroutines.launch
import presentation.components.AddBookDialog
import presentation.components.SearchBookDialog
import presentation.components.TableRow
import presentation.components.ToolStrip
import presentation.viewmodel.AppViewModel

@Composable
fun HashTableScreen(vm: AppViewModel) {
    val showAddDialog = remember { mutableStateOf(false) }
    val showSearchDialog = remember { mutableStateOf(false) }
    val lazyState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val searchBook = remember { mutableStateOf(Book("", "", "")) }

    Scaffold(
        topBar = {
            ToolStrip(
                title = "Книги",
                onAdd = { showAddDialog.value = true },
                onDelete = { vm.deleteBook() },
                onSearch = {
                    coroutineScope.launch {
                        vm.searchBook()?.let { searchBook.value = it }
                        showSearchDialog.value = true
                    }
                },
                onSave = { vm.saveBooks() },
                searchBar = {
                    TextField(
                        value = vm.bookField.value,
                        onValueChange = { vm.bookField.value = it },
                        label = { Text("Введите ISBN") }
                    )
                }
            )
        }
    ) {
        SelectionContainer {
            LazyColumn(
                state = lazyState
            ) {
                itemsIndexed(vm.rows.value) { index, entry ->
                    val ib = entry.value
                    if (ib == null) TableRow(index, "", null, null)
                    else TableRow(index, ib.toString(), vm.books.value[ib], entry.status)
                }
            }
        }
    }
    if (showAddDialog.value) {
        AddBookDialog({ showAddDialog.value = false }, vm)
    }
    if (showSearchDialog.value) {
        SearchBookDialog({ showSearchDialog.value = false }, searchBook.value)
    }
}
