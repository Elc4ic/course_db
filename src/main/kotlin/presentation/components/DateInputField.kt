package presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import data.format
import java.time.LocalDate
import java.util.Date

@Composable
fun DateInputField(
    label: String,
    selectedDate: Date?,
    onDateSelected: (Date) -> Unit,
) {
    var showDateSelector by remember { mutableStateOf(false) }
    var year by remember { mutableStateOf(selectedDate?.year ?: 2023) }
    var month by remember { mutableStateOf(selectedDate?.month ?: 1) }
    var day by remember { mutableStateOf(selectedDate?.day ?: 1) }

    val daysInMonth = remember(month, year) {
        LocalDate.of(year, month, 1).lengthOfMonth()
    }

    TextField(
        value = format.format(selectedDate),
        label = { Text(label) },
        onValueChange = {},
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showDateSelector = true }) {
                Icon(Icons.Default.DateRange, "Select Date")
            }
        },
        modifier = Modifier.width(250.dp)
    )

    if (showDateSelector) {
        Dialog(onDismissRequest = { showDateSelector = false }) {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val ss = LocalDate.now().year
                        Text("Year:", modifier = Modifier.width(80.dp))
                        Slider(
                            value = year.toFloat(),
                            onValueChange = { year = it.toInt() },
                            valueRange = 1900f..ss.toFloat(),
                            steps = ss - 1900,
                            modifier = Modifier.weight(1f)
                        )
                        Text(year.toString())
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Month:", modifier = Modifier.width(80.dp))
                        Slider(
                            value = month.toFloat(),
                            onValueChange = { month = it.toInt() },
                            valueRange = 1f..12f,
                            steps = 11,
                            modifier = Modifier.weight(1f)
                        )
                        Text(month.toString())
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Day:", modifier = Modifier.width(80.dp))
                        Slider(
                            value = day.toFloat(),
                            onValueChange = { day = it.toInt() },
                            valueRange = 1f..daysInMonth.toFloat(),
                            steps = daysInMonth - 1,
                            modifier = Modifier.weight(1f)
                        )
                        Text(day.toString())
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDateSelector = false }) {
                            Text("Cancel")
                        }
                        TextButton(onClick = {
                            onDateSelected(Date(year - 1900, month, day))
                            showDateSelector = false
                        }) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}