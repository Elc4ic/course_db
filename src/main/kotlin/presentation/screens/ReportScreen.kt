package presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import presentation.components.DateInputField
import presentation.components.TableReportRow
import presentation.components.ToastDialog
import presentation.components.ToolReportStrip
import presentation.viewmodel.AppViewModel
import presentation.viewmodel.WindowViewModel

@Composable
fun ReportScreen(key: String,vm: AppViewModel,wvm: WindowViewModel) {
    var toastMessage by remember { mutableStateOf("") }
    var showToast by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            ToolReportStrip(
                windowSelector = {wvm.selector(key)},
                onSearch = { vm.filter() },
                onSave = { vm.saveReport() },
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
    if (showToast) {
        ToastDialog(toastMessage) { showToast = false }
        LaunchedEffect(Unit) { delay(1000); showToast = false }
    }
}