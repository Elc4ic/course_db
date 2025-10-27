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
fun DateRangePicker(
    label: String,
    startDate: Date?,
    endDate: Date?,
    onRangeSelected: (start: Date, end: Date) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDateSelector by remember { mutableStateOf(false) }

    TextField(
        modifier = modifier,
        value = "${format.format(startDate)} - ${format.format(endDate)}",
        label = { Text(label) },
        onValueChange = {},
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showDateSelector = true }) {
                Icon(Icons.Default.DateRange, "Select Date")
            }
        },
    )
    DateRangeDialog(showDateSelector, startDate, endDate, onDismiss = { showDateSelector = false }, onRangeSelected)
}

@Composable
fun DateRangeDialog(
    show: Boolean,
    startDate: Date?,
    endDate: Date?,
    onDismiss: () -> Unit,
    onRangeSelected: (start: Date, end: Date) -> Unit
) {
    if (!show) return

    var startYear by remember { mutableStateOf(startDate?.year ?: LocalDate.now().year) }
    var startMonth by remember {
        mutableStateOf(
            (startDate?.month?.plus(1) ?: LocalDate.now().monthValue).coerceIn(1, 12)
        )
    }
    var startDay by remember { mutableStateOf(startDate?.day ?: 1) }

    var endYear by remember { mutableStateOf(endDate?.year ?: LocalDate.now().year) }
    var endMonth by remember { mutableStateOf((endDate?.month?.plus(1) ?: LocalDate.now().monthValue).coerceIn(1, 12)) }
    var endDay by remember { mutableStateOf(endDate?.day ?: LocalDate.now().dayOfMonth) }

    var showErrors by remember { mutableStateOf(false) }
    val errors = mutableListOf<String>()

    val startDaysInMonth = remember(startYear, startMonth) { YearMonth.of(startYear, startMonth).lengthOfMonth() }
    val endDaysInMonth = remember(endYear, endMonth) { YearMonth.of(endYear, endMonth).lengthOfMonth() }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Выбор периода")
                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {

                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.weight(1.0f)) {
                        Text("Начало:")
                        DropdownSelector(
                            label = "Год",
                            selected = max(1900, startYear).toString(),
                            items = (1900..LocalDate.now().year).map { it.toString() }.reversed(),
                            onSelect = {
                                startYear = it.toInt()
                                if (startDay > YearMonth.of(startYear, startMonth).lengthOfMonth()) startDay =
                                    YearMonth.of(startYear, startMonth).lengthOfMonth()
                            })
                        Spacer(Modifier.width(8.dp))
                        DropdownSelector(
                            label = "Месяц",
                            selected = startMonth.toString(),
                            items = (1..12).map { it.toString() },
                            onSelect = {
                                startMonth = it.toInt()
                                if (startDay > YearMonth.of(startYear, startMonth).lengthOfMonth()) startDay =
                                    YearMonth.of(startYear, startMonth).lengthOfMonth()
                            })
                        Spacer(Modifier.width(8.dp))
                        DropdownSelector(
                            label = "День",
                            selected = startDay.toString(),
                            items = (1..startDaysInMonth).map { it.toString() },
                            onSelect = { startDay = it.toInt() })
                    }

                    Text("---", modifier = Modifier.padding(8.dp))

                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.weight(1.0f)) {
                        Text("Конец:")
                        DropdownSelector(
                            label = "Год",
                            selected = max(1900, endYear).toString(),
                            items = (1900..LocalDate.now().year).map { it.toString() }.reversed(),
                            onSelect = {
                                endYear = it.toInt()
                                if (endDay > YearMonth.of(endYear, endMonth).lengthOfMonth()) endDay =
                                    YearMonth.of(endYear, endMonth).lengthOfMonth()
                            })
                        Spacer(Modifier.width(8.dp))
                        DropdownSelector(
                            label = "Месяц",
                            selected = endMonth.toString(),
                            items = (1..12).map { it.toString() },
                            onSelect = {
                                endMonth = it.toInt()
                                if (endDay > YearMonth.of(endYear, endMonth).lengthOfMonth()) endDay =
                                    YearMonth.of(endYear, endMonth).lengthOfMonth()
                            })
                        Spacer(Modifier.width(8.dp))
                        DropdownSelector(
                            label = "День",
                            selected = endDay.toString(),
                            items = (1..endDaysInMonth).map { it.toString() },
                            onSelect = { endDay = it.toInt() })
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (showErrors) {
                    Column {
                        errors.forEach {
                            Text(it)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = onDismiss) { Text("Отмена") }
                    Button(onClick = {
                        val start = Date(if (startYear > 1900) startYear - 1900 else 0, startMonth - 1, startDay)
                        val end = Date(if (endYear > 1900) endYear - 1900 else 0, endMonth - 1, endDay)
                        if (start > end) {
                            errors.add("Невозможный период")
                            showErrors = true
                        } else {
                            showErrors = false
                            errors.clear()
                            onRangeSelected(start, end)
                            onDismiss()
                        }
                    }) {
                        Text("Применить")
                    }
                }
            }
        }
    }
}