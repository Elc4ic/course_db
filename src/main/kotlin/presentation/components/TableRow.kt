package presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.onClick
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import entities.Book
import entities.ReportField

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TableRow(index: Int, value: String, book: Book?, status: Boolean?, onDelete: (Book) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().onClick(
            onLongClick = {
                book?.let { onDelete(it) }
            },
            onClick = {},
        ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TableCell(index.toString(), 0.1f)
        TableCell(book?.isbn ?: "-", 0.7f, status == true)
        TableCell(value, 0.1f, status == true)
        TableCell(book?.title ?: "-", 0.7f, status == true)
        TableCell(book?.author ?: "-", 0.7f, status == true)
    }
}

@Composable
fun HeaderTableRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TableCell("№", 0.1f,true,2.dp)
        TableCell("ISBN", 0.7f, true,2.dp)
        TableCell("i", 0.1f, true,2.dp)
        TableCell("Название", 0.7f, true,2.dp)
        TableCell("Автор", 0.7f, true,2.dp)
    }
}

@Composable
fun TableReportRow(index: Int, row: ReportField) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TableCell(index.toString(), 0.3f)
        TableCell(row.isbn, 1f)
        TableCell(row.title, 1f)
        TableCell(row.author, 1f)
        TableCell(row.invNum, 1f)
        TableCell(row.status, 1f)
        TableCell(row.date, 1f)
    }
}

@Composable
fun RowScope.TableCell(text: String, weight: Float, off: Boolean = true, border: Dp = 1.dp) {
    Text(
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        text = text,
        color = if (off) MaterialTheme.colors.onBackground else MaterialTheme.colors.onPrimary,
        modifier = Modifier
            .border(border, color = MaterialTheme.colors.onBackground)
            .padding(8.dp).weight(weight, true)
    )
}