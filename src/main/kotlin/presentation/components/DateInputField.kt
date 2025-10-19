package presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import data.format
import java.time.LocalDate
import java.time.YearMonth
import java.util.*
import kotlin.math.max

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
            Card(modifier = Modifier.padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Выбор даты")
                    Spacer(Modifier.height(16.dp))

                    DropdownSelector(
                        label = "Год",
                        selected = max(1900, year).toString(),
                        items = (1900..LocalDate.now().year).map { it.toString() }.reversed(),
                        onSelect = {
                            year = it.toInt(); if (day > YearMonth.of(year, month).lengthOfMonth()) day =
                            YearMonth.of(year, month).lengthOfMonth()
                        }
                    )

                    DropdownSelector(
                        label = "Месяц",
                        selected = month.toString(),
                        items = (1..12).map { it.toString() },
                        onSelect = {
                            month = it.toInt(); if (day > YearMonth.of(year, month).lengthOfMonth()) day =
                            YearMonth.of(year, month).lengthOfMonth()
                        }
                    )

                    DropdownSelector(
                        label = "День",
                        selected = day.toString(),
                        items = (1..daysInMonth).map { it.toString() },
                        onSelect = { day = it.toInt() }
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(onClick = { showDateSelector = false }) { Text("Отмена") }
                        Button(onClick = {
                            val date = Date(if (year > 1900) year - 1900 else 1900, month - 1, day)
                            onDateSelected(date)
                            showDateSelector = false
                        }) { Text("Применить") }
                    }
                }
            }
        }
    }
}