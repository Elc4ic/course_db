package presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ToolStrip(
    onAdd: () -> Unit = {},
    onDelete: () -> Unit = {},
    onSearch: () -> Unit = {},
    onSave: () -> Unit = {},
    searchBar: @Composable () -> Unit = {},
    windowSelector: @Composable () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    TopAppBar(
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                windowSelector()
                Button(onClick = { expanded = true }) {
                    Text("Действия")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            onSave()
                        }
                    ) { Text("Сохранить") }
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            onAdd()
                        }
                    ) { Text("Добавить") }
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            onDelete()
                        }
                    ) { Text("Удалить") }
                }
                searchBar()
                IconButton(onClick = onSearch) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }

        }
    )
}

@Composable
fun ToolReportStrip(
    onSearch: () -> Unit,
    onSave: () -> Unit,
    searchBar: @Composable () -> Unit = {},
    windowSelector: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = {
            var expanded by remember { mutableStateOf(false) }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                windowSelector()

                Button(onClick = { expanded = !expanded }) {
                    Text("Фильтры")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    Box(Modifier.padding(12.dp)) {
                        searchBar()
                    }
                }
                Button(onClick = onSearch) {
                    Text("Поиск")
                }
                Button(onClick = onSave) {
                    Text("Сохранить")
                }
            }
        }
    )
}