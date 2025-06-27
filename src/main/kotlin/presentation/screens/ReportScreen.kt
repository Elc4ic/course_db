package presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import presentation.components.DateInputField
import presentation.components.TableReportRow
import presentation.components.ToolReportStrip
import presentation.viewmodel.AppViewModel

@Composable
fun ReportScreen(vm: AppViewModel) {
    Scaffold(
        topBar = {
            ToolReportStrip(
                title = "Отчет",
                onSearch = { vm.filter() },
                searchBar = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextField(
                            value = vm.filterAuthorField.value,
                            onValueChange = { vm.filterAuthorField.value = it },
                            label = { Text("Введите автора") }
                        )
                        TextField(
                            value = vm.filterInvNumField.value,
                            onValueChange = { vm.filterInvNumField.value = it },
                            label = { Text("Введите инвентарный номер") }
                        )
                        DateInputField(
                            label = "от",
                            selectedDate = vm.filterFromField.value,
                            onDateSelected = { date ->
                                vm.filterFromField.value = date
                            }
                        )
                        DateInputField(
                            label = "до",
                            selectedDate = vm.filterToField.value,
                            onDateSelected = { date ->
                                vm.filterToField.value = date
                            }
                        )
                    }
                }
            )
        },
    ) {
        LazyColumn {
            itemsIndexed(vm.report.value) { index, row ->
                TableReportRow(index, row)
            }
        }
    }
}