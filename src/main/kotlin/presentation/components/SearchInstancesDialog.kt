package presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import entities.Instance

@Composable
fun SearchInstanceDialog(
    onDismiss: () -> Unit,
    showToast: () -> Unit,
    instances: List<Instance>,
    onDelete: (Instance) -> Unit
) {
    var list by remember { mutableStateOf(instances) }
    showToast()
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            SelectionContainer {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Экземпляры", style = MaterialTheme.typography.h6)
                    }
                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        list.forEach { instance ->
                            InstanceRow(instance) {
                                onDelete(instance)
                                list = list - instance
                            }
                        }
                    }
                    Button(onClick = { onDismiss() }) {
                        Text("Закрыть")
                    }
                }
            }
        }
    }
}

@Composable
fun InstanceRow(instance: Instance, onDelete: () -> Unit) {
    Card {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Text("ISBN: ${instance.isbn}")
                Text("Инвентарный номер: ${instance.inventoryNumber}")
                Text("Статус: ${instance.status}")
                Text("Дата: ${instance.date}")
            }
            IconButton(onClick = { onDelete() }) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить")
            }
        }
    }
}