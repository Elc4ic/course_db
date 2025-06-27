package entities.forms

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
        isValid = false

        errors.clear()
        ISBN.validateISBN(isbn, errors)
        if(vm.hashTable.value.get(isbn) == null){
            errors += "Нет книги с таким ISBN"
        }

        try {
            val i = invNum.toInt()
            when {
                invNum.isBlank() -> errors += "Инвентарный номер пуст\n"
                i < 0 -> errors += "Не валидный инвентарный номер\n"
            }
        } catch (e: Exception) {
            errors += "Не валидный инвентарный номер\n"
        }
        if (status.isBlank()) errors += "Нет статуса\n"
        if (errors.isEmpty()) isValid = true
    }

    fun getInstance(): Instance {
        return Instance(isbn, invNum, status, format.format(date))
    }
}