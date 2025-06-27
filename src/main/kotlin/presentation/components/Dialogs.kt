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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import data.status
import entities.Book
import entities.forms.BookForm
import entities.forms.InstanceForm
import presentation.viewmodel.AppViewModel

@Composable
fun AddBookDialog(onDismiss: () -> Unit, vm: AppViewModel) {
    val form = mutableStateOf(BookForm())

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text("Форма ввода книги", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = form.value.isbn,
                    onValueChange = {  form.value = form.value.copy(isbn = it) },
                    label = { Text("ISBN") }
                )
                OutlinedTextField(
                    value = form.value.title,
                    onValueChange = {  form.value = form.value.copy(title =  it) },
                    label = { Text("Название") }
                )
                OutlinedTextField(
                    value = form.value.author,
                    onValueChange = {  form.value = form.value.copy(author =  it) },
                    label = { Text("Автор") }
                )
                if (form.value.errors.isNotEmpty()) {
                    Column {
                        form.value.errors.forEach {
                            Text(it)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    form.value.validate()
                    if (form.value.isValid) {
                        val book = Book(form.value.isbn, form.value.title, form.value.author)
                        vm.addBook(book)
                        onDismiss()
                    }
                    form.value = form.value.copy()
                }) {
                    Text("Вставить")
                }
            }
        }
    }
}

@Composable
fun AddInstanceDialog(onDismiss: () -> Unit, vm: AppViewModel) {
    var form = mutableStateOf(InstanceForm())

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text("Форма ввода книги", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = form.value.isbn,
                    onValueChange = { form.value = form.value.copy(isbn = it) },
                    label = { Text("ISBN") }
                )
                OutlinedTextField(
                    value = form.value.invNum,
                    onValueChange = { form.value = form.value.copy(invNum = it) },
                    label = { Text("Инвентарный номер") }
                )
                DropdownSelector(
                    label = "Статус",
                    items = status,
                    selected = form.value.status,
                    onSelect = {
                        form.value = form.value.copy(status =  it)
                    },
                )
                DateInputField(
                    label = "Дата",
                    selectedDate = form.value.date,
                    onDateSelected = {
                        form.value = form.value.copy(date = it)
                    },
                )
                if (form.value.errors.isNotEmpty()) {
                    Column {
                        form.value.errors.forEach {
                            Text(it)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    form.value.validate(vm)
                    if (form.value.isValid) {
                        println("ffff")
                        vm.addInstance(form.value.getInstance())
                        onDismiss()
                    }
                    form.value = form.value.copy()
                }) {
                    Text("Вставить")
                }
            }
        }
    }
}