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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import data.status
import entities.Book
import entities.forms.BookForm
import entities.forms.InstanceForm
import presentation.viewmodel.AppViewModel

@Composable
fun AddBookDialog(onDismiss: () -> Unit, showToast: () -> Unit, vm: AppViewModel) {
    var form by remember { mutableStateOf(BookForm()) }
    var showErrors by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text("Форма ввода книги", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = form.isbn,
                    onValueChange = { form = form.copy(isbn = it) },
                    label = { Text("ISBN") }
                )
                OutlinedTextField(
                    value = form.title,
                    onValueChange = { form = form.copy(title = it) },
                    label = { Text("Название") }
                )
                OutlinedTextField(
                    value = form.author,
                    onValueChange = { form = form.copy(author = it) },
                    label = { Text("Автор") }
                )
                if (showErrors) {
                    Column {
                        form.errors.forEach {
                            Text(it)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    showErrors = false
                    form.validate()
                    if (form.isValid) {
                        val book = Book(form.isbn, form.title, form.author)
                        vm.addBook(book)
                        showToast()
                        onDismiss()
                    }
                    form = form.copy()
                    showErrors = true
                }) {
                    Text("Вставить")
                }
            }
        }
    }
}

@Composable
fun AddInstanceDialog(onDismiss: () -> Unit, showToast: () -> Unit, vm: AppViewModel) {
    var form by remember { mutableStateOf(InstanceForm()) }
    var showErrors by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text("Форма ввода экземпляра", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = form.isbn,
                    onValueChange = { form = form.copy(isbn = it) },
                    label = { Text("ISBN") }
                )
                OutlinedTextField(
                    value = form.invNum,
                    onValueChange = { form = form.copy(invNum = it) },
                    label = { Text("Инвентарный номер") }
                )
                DropdownSelector(
                    label = "Статус",
                    items = status,
                    selected = form.status,
                    onSelect = {
                        form = form.copy(status = it)
                    },
                )
                DateInputField(
                    label = "Дата",
                    selectedDate = form.date,
                    onDateSelected = {
                        form = form.copy(date = it)
                    },
                )
                if (showErrors) {
                    form.errors.forEach {
                        Text(it)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    showErrors = false
                    form.validate(vm)
                    if (form.isValid) {
                        vm.addInstance(form.getInstance())
                        showToast()
                        onDismiss()
                    }
                    form = form.copy()
                    showErrors = true
                }) {
                    Text("Вставить")
                }
            }
        }
    }
}