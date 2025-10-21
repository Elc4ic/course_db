package presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import entities.Book

@Composable
fun SearchBookDialog(onDismiss: () -> Unit, showToast: () -> Unit, book: Book) {
    Dialog(onDismissRequest = onDismiss) {
        showToast()
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Книга", style = MaterialTheme.typography.h6)
                }
                Text("ISBN: ${book.isbn}", style = MaterialTheme.typography.h6)
                Text("Название: ${book.title}", style = MaterialTheme.typography.h6)
                Text("Автор: ${book.author}", style = MaterialTheme.typography.h6)

                Button(onClick = {
                    onDismiss()
                }) {
                    Text("Закрыть")
                }
            }
        }
    }
}