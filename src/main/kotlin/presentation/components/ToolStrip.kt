package presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.nio.channels.Selector

@Composable
fun ToolStrip(
    onAdd: () -> Unit = {},
    onDelete: () -> Unit = {},
    onSearch: () -> Unit = {},
    onSave: () -> Unit = {},
    searchBar: @Composable () -> Unit = {},
    windowSelector: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = {
            windowSelector()
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
    onSearch: () -> Unit,
    onSave: () -> Unit,
    searchBar: @Composable () -> Unit = {},
    windowSelector: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = {
            windowSelector()
            IconButton(onClick = onSave) {
                Icon(Icons.Default.Done, contentDescription = "Save")
            }
            searchBar()
            IconButton(onClick = onSearch) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }
    )
}