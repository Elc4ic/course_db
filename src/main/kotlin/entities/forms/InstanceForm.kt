package entities.forms

import data.LogsFile
import data.format
import entities.Instance
import presentation.viewmodel.AppViewModel
import java.util.Date

data class InstanceForm(
    var isbn: String = "",
    var invNum: String = "",
    var status: String = "",
    var date: Date = Date(),
    var errors: MutableList<String> = mutableListOf<String>(),
    var isValid: Boolean = false,
) {
    fun validate(vm: AppViewModel) {
        errors.clear()
        isValid = false

        ISBN.validateISBN(isbn, errors)
        if(vm.hashTable.get(isbn) == null){
            errors += "Нет книги с таким ISBN"
        }

        try {
            val i = invNum.toInt()
            when {
                invNum.isBlank() -> errors += "Инвентарный номер пуст"
                i < 0 -> errors += "Не валидный инвентарный номер"
            }
        } catch (e: Exception) {
            errors += "Не валидный инвентарный номер"
        }
        if (status.isBlank()) errors += "Нет статуса"
        errors.forEach{ LogsFile.writeln("Ошибка ввода экземпляра книги: " + it) }
        if (errors.isEmpty()) isValid = true
    }

    fun getInstance(): Instance {
        return Instance(isbn, invNum, status, format.format(date))
    }
}