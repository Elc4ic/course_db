package presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.CoroutineScope
import presentation.viewmodel.AppViewModel

@Composable
fun DeleteBookDialog(
    onDismiss: () -> Unit,
    showToast: () -> Unit,
    vm: AppViewModel, scope: CoroutineScope, toast: ToastState
) {
    var isbn by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text("Форма удаления книги", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = isbn,
                    onValueChange = { isbn = it },
                    label = { Text("ISBN") }
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название") }
                )
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Автор") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    vm.deleteBook(isbn, title, author, scope, toast)?.let { if (it) showToast() }
                    onDismiss()
                }) {
                    Text("Удалить")
                }
            }
        }
    }
}

@Composable
fun DeleteInstanceDialog(
    onDismiss: () -> Unit,
    showToast: () -> Unit,
    vm: AppViewModel, scope: CoroutineScope, toast: ToastState
) {
    var isbn by remember { mutableStateOf("") }
    var invNum by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text("Форма удаления экземпляра", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = isbn,
                    onValueChange = { isbn = it },
                    label = { Text("ISBN") }
                )
                OutlinedTextField(
                    value = invNum,
                    onValueChange = { invNum = it },
                    label = { Text("Инвентарный номер") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    vm.deleteInstance(isbn, invNum, scope, toast)?.let { if (it) showToast() }
                    onDismiss()
                }) {
                    Text("Удалить")
                }
            }
        }
    }
}