package entities

data class Book(
    val isbn: String,
    val title: String,
    val author: String
){
    override fun toString(): String {
        return "Book(isbn='$isbn', title='$title', author='$author')"
    }
}