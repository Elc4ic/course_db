package data

fun <T> withToastHandling(
    toaster: (String, Boolean) -> Unit,
    catchable: () -> T
): T? {
    return try {
        catchable()
    } catch (e: Exception) {
        toaster(e.message ?: "Неизвестная ошибка", true)
        null
    }
}

object Errors {
    //ADD
    const val ADD_BOOK = "Ошибка добавления книги"
    const val ADD_INSTANCE = "Ошибка добавления экземпляра"

    //DELETE
    const val DELETE_INSTANCE = "Ошибка удаления экземпляра"
    const val DELETE_BOOK = "Ошибка удаления книги"

    //SEARCH
    const val NO_SUCH_BOOK = "Книга не найдена"
    const val NO_SUCH_INSTANCES = "Экземпляры не найдена"

    //SAVE
    const val SAVE_BOOK_FILE = "Ошибка обновления книг"
    const val SAVE_REPORT_FILE = "Ошибка сохранения отчета"
    const val SAVE_INSTANCE_FILE = "Ошибка сохранения базы экземпляров"

    //FILTER
    const val FILTER_ERROR = "Ошибка фильтрации"

}