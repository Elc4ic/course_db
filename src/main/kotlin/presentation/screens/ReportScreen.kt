package presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.reportPath
import kotlinx.coroutines.launch
import presentation.components.DateRangePicker
import presentation.components.TableReportRow
import presentation.components.ToastContainer
import presentation.components.ToastHost
import presentation.components.ToolReportStrip
import presentation.components.rememberToastState
import presentation.viewmodel.AppViewModel
import presentation.viewmodel.WindowViewModel

@Composable
fun ReportScreen(key: String, vm: AppViewModel, wvm: WindowViewModel) {
    val nothing = "Ничего не найдено"
    var info by remember { mutableStateOf(nothing) }


    ToastContainer { scope,toast, toaster ->
        Scaffold(
            topBar = {
                ToolReportStrip(
                    windowSelector = { wvm.selector(key) },
                    onSearch = {
                        vm.filter(scope, toast)
                        info =
                            "Фильтры: ${vm.filterAuthorField.value}, " +
                                    "${vm.filterInvNumField.value}, " +
                                    "${vm.filterFromField.value} -- " +
                                    "${vm.filterToField.value}.\n " +
                                    "Поиск: $nothing"
                        toaster("Поиск прошел успешно", false)
                    },
                    onSave = {
                        vm.saveReport(scope, toast)
                        toaster("Отчет сохранен по пути: $reportPath", false)
                    },
                    searchBar = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.width(400.dp).height(200.dp)
                        ) {
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = vm.filterAuthorField.value,
                                onValueChange = { vm.filterAuthorField.value = it },
                                label = { Text("Введите автора") }
                            )
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = vm.filterInvNumField.value,
                                onValueChange = { vm.filterInvNumField.value = it },
                                label = { Text("Введите инвентарный номер") }
                            )
                            DateRangePicker(
                                modifier = Modifier.fillMaxWidth(),
                                label = "Период",
                                startDate = vm.filterFromField.value,
                                endDate = vm.filterToField.value,
                                onRangeSelected = { start, end ->
                                    vm.filterFromField.value = start
                                    vm.filterToField.value = end
                                }
                            )
                        }
                    }
                )
            },
        ) {
            if (vm.report.value.isEmpty()) {
                Row(
                    modifier = Modifier.padding(30.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) { Text(info) }
            }
            LazyColumn {
                itemsIndexed(vm.report.value) { index, row ->
                    TableReportRow(index, row)
                }
            }
        }
    }
}