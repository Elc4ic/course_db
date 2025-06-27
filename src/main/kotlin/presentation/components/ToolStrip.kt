package presentation.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*

@Composable
fun ToolStrip(
    title: String,
    onAdd: () -> Unit = {},
    onDelete: () -> Unit = {},
    onSearch: () -> Unit = {},
    onSave: () -> Unit = {},
    searchBar: @Composable () -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(title)
            IconButton(onClick = onSave) {
                Icon(Icons.Default.Done, contentDescription = "Save")
            }
            IconButton(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
            searchBar()
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
            IconButton(onClick = onSearch) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }
    )
}

@Composable
fun ToolReportStrip(
    title: String,
    onSearch: () -> Unit,
    searchBar: @Composable () -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(title)
            searchBar()
            IconButton(onClick = onSearch) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }
    )
}