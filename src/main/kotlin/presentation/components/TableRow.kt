package presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import entities.Book
import entities.ReportField

@Composable
fun TableRow(index: Int, value: String, book: Book?, status: Boolean?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TableCell(index.toString(), 0.1f)
        TableCell(book?.isbn ?: "-", 0.5f, status == true)
        TableCell(value, 0.1f, status == true)
        TableCell(book?.title ?: "-", 0.6f, status == true)
        TableCell(book?.author ?: "-", 0.6f, status == true)
        TableCell((if (status == true) "1 " else "0"), 0.1f, status == true)
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
fun RowScope.TableCell(text: String, weight: Float, off: Boolean = true) {
    Text(
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        text = text,
        color = if (off) Color.Black else Color.LightGray,
        modifier = Modifier
            .border(1.dp, Color.Gray)
            .padding(8.dp).weight(weight, true)
    )
}