package entities.forms

data class BookForm(
    var isbn: String = "",
    var title: String = "",
    var author: String = "",
    var errors: MutableList<String> = mutableListOf(),
    var isValid: Boolean = false,
) {
    fun validate() {
        errors.clear()
        isValid = false

        errors.clear()
        ISBN.validateISBN(isbn, errors)

        if (title.isBlank()) errors += "Нет названия\n"
        if (author.isBlank()) errors += "Нет автора\n"
        if (errors.isEmpty()) isValid = true
    }
}

object ISBN {
    fun validateISBN(isbn: String, errors: MutableList<String>) {
        when {
            isbn.isBlank() -> errors.add("ISBN is empty")
            !isValidISBNFormat(isbn) -> errors.add("ISBN has invalid format")
            !isValidISBNChecksum(isbn) -> errors.add("ISBN checksum is invalid")
        }
    }

    fun isValidISBNFormat(isbn: String): Boolean {
        return isbn.matches(Regex("""^(?:\d[\s-]?){9}[\dXx]$|^(?:\d[\s-]?){12}[\d]$"""))
    }

    fun isValidISBNChecksum(isbn: String): Boolean {
        val cleanIsbn = isbn.replace(Regex("""[\s-]"""), "")
        return when (cleanIsbn.length) {
            10 -> validateISBN10(cleanIsbn)
            13 -> validateISBN13(cleanIsbn)
            else -> false
        }
    }

    fun validateISBN10(isbn: String): Boolean {
        var sum = 0
        for (i in 0..9) {
            val c = isbn[i]
            sum += when {
                c == 'X' || c == 'x' -> 10 * (10 - i)
                c.isDigit() -> c.toString().toInt() * (10 - i)
                else -> return false
            }
        }
        return sum % 11 == 0
    }

    fun validateISBN13(isbn: String): Boolean {
        var sum = 0
        for (i in 0..12) {
            val digit = isbn[i].toString().toIntOrNull() ?: return false
            sum += digit * if (i % 2 == 0) 1 else 3
        }
        return sum % 10 == 0
    }
}
