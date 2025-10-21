package presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import entities.Instance

@Composable
fun SearchInstanceDialog(
    onDismiss: () -> Unit,
    showToast: () -> Unit,
    instances: List<Instance>
) {
    showToast()
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Экземпляры", style = MaterialTheme.typography.h6)
                }
                Column(modifier = Modifier.padding(8.dp)) {
                    instances.forEach { instance ->
                        InstanceRow(instance)
                    }
                }
                Button(onClick = {
                    onDismiss()
                }) {
                    Text("Закрыть")
                }
            }
        }
    }
}

@Composable
fun InstanceRow(instance: Instance) {
    SelectionContainer(modifier = Modifier.padding(2.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text("ISBN: ${instance.isbn}")
            Text("Инвентарный номер: ${instance.inventoryNumber}")
            Text("Статус: ${instance.status}")
            Text("Дата: ${instance.date}")
            Divider(thickness = 1.dp, color = Color.Black)
        }
    }
}