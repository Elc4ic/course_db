package entities

import data.format

enum class InstanceStatus {
    AVAILABLE,
    RESERVED,
    BORROWED,
    LOST,
    DAMAGED,
    NULL
}

data class Instance(
    val isbn: String,
    val inventoryNumber: String,
    val status: String,
    val date: String,
) {
    override fun toString(): String {
        return "Instance(isbn='$isbn', inventoryNumber=$inventoryNumber, status=$status, date='$date')"
    }

    val dateF = format.parse(date)
}