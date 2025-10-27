package presentation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import data.*
import entities.Book
import entities.Instance
import entities.ReportField
import kotlinx.coroutines.CoroutineScope
import presentation.components.ToastState
import structures.HashTable
import structures.HashTable.Entry
import structures.RedBlackTree
import structures.RedBlackTree.Check
import java.util.*


class AppViewModel {
    private lateinit var paths: Pair<String?, String?>
    private lateinit var _books: MutableState<Array<Book?>>
    private lateinit var _instances: MutableState<Array<Instance?>>
    private val _report = mutableStateOf(arrayOf<ReportField>())
    private lateinit var toaster: (CoroutineScope, ToastState) -> (String, Boolean) -> Unit

    val bookField = mutableStateOf("")
    val instanceField = mutableStateOf("")
    val filterAuthorField = mutableStateOf("")
    val filterInvNumField = mutableStateOf("")
    val filterFromField = mutableStateOf(Date())
    val filterToField = mutableStateOf(Date())

    private val _hashTable = mutableStateOf(HashTable())
    private val _rows = mutableStateOf(arrayOf<Entry>())
    private val _tree = mutableStateOf(RedBlackTree<String>())
    var treeVersion = mutableStateOf(0)
        private set
    private val _filterTree = mutableStateOf(RedBlackTree<Date>())

    fun update() {
        paths = FileUtils.getPaths()
        _books = mutableStateOf(FileUtils.initBooksFromFile(paths.first!!))
        _instances = mutableStateOf(FileUtils.initInstancesFromFile(paths.second!!))
        initHashTable()
        initTree()
        initFilterTree()
    }

    private fun initHashTable() {
        hashTable.clear()
        hashTable.initFromArray(books) { it.isbn }
        _rows.value = hashTable.rows
        LogsFile.writeln(hashTable.toString())
    }

    private fun initTree() {
        tree.initFromArray(_instances.value) { it.isbn }
        LogsFile.writeln("\n" + tree.toString())
    }

    private fun initFilterTree() {
        filterTree.initFromArray(_instances.value) { it.dateF }
        LogsFile.writeln("\n" + filterTree.toString())
    }

    fun setToaster(toaster: (CoroutineScope, ToastState) -> (String, Boolean) -> Unit) {
        this.toaster = toaster
    }

    //books functions
    fun addBook(book: Book, t: (String, Boolean) -> Unit) = withToastHandling(t) {
        if (hashTable.get(book.isbn) != null) throw DuplicateFormatFlagsException("Введеная книга дубликат существующей")
        _books.value += book
        hashTable.put(book.isbn, books.size - 1)
        _rows.value = hashTable.rows.clone()
        LogsFile.writeln("Книга добавленна: $book")
        true
    }

    fun deleteBook(isbn: String, title: String, author: String, t: (String, Boolean) -> Unit) =
        withToastHandling(t) {
            val index = hashTable.get(isbn) ?: throw NoSuchElementException("Такой книги нет")
            val book = books[index] ?: throw NoSuchElementException("Такой книги нет в базе")
            if (book.title != title || book.author != author) throw NoSuchElementException("Автор или название не соответствуют указанному ISBN")

            val lastBook = books.last()
            hashTable.rows.first { it.key == lastBook?.isbn }.value = index
            hashTable.remove(book.isbn, index)
            books[index] = lastBook
            _books.value = books.copyOfRange(0, books.size - 1)
            LogsFile.writeln("Удаление книги: $book")
            deleteInTreeByBook(isbn)

            _rows.value = hashTable.rows
            true
        }

    private fun deleteInTreeByBook(isbn: String) {
        tree.search(isbn)?.duplicates?.forEach { ni ->
            val lastInstance = _instances.value.last()
            val instance = _instances.value[ni]
            tree.search(lastInstance?.isbn!!)?.duplicates?.update(_instances.value.size - 1, ni)
            tree.delete(isbn, ni)
            instance?.dateF?.let {
                filterTree.search(lastInstance.dateF!!)?.duplicates?.update(_instances.value.size - 1, ni)
                filterTree.delete(it, ni)
            }
            _instances.value[ni] = _instances.value.last()
            _instances.value = _instances.value.copyOfRange(0, _instances.value.size - 1)
            LogsFile.writeln("Также удаляется экземпляр: $instance")
        }
    }

    fun searchBook(t: (String, Boolean) -> Unit) = withToastHandling(t) {
        val book = hashTable.get(bookField.value)?.let { books[it] }
            ?: throw NoSuchElementException("Нет такой книги")
        LogsFile.writeln("Найдена книга: $book")
        book
    }

    fun saveBooks(t: (String, Boolean) -> Unit) = withToastHandling(t) {
        FileUtils.saveBooksToFile(paths.first!!, books)
        LogsFile.writeln("Файл книг обновлен")
        true
    }

    fun checkInstanceOnDuplicate(instance: Instance): Boolean {
        val node = tree.search(instance.isbn)
        return node?.duplicates?.any { instances[it] == instance } ?: false
    }

    //instances functions
    fun addInstance(instance: Instance, t: (String, Boolean) -> Unit) = withToastHandling(t) {
        if (checkInstanceOnDuplicate(instance)) throw DuplicateFormatFlagsException("Введеный экземпляр дубликат существующего")
        _instances.value += instance
        tree.add(instance.isbn, instances.size - 1)
        filterTree.add(instance.dateF, instances.size - 1)
        LogsFile.writeln("Экземпляр добавлен:${instance}")
        treeVersion.value++
        true
    }


    fun deleteInstance(isbn: String, invNum: String, t: (String, Boolean) -> Unit) =
        withToastHandling(t) {
            var count = 0
            val index = tree.search(isbn)?.duplicates?.first {
                invNum == _instances.value[it]?.inventoryNumber
            }
            index?.let {
                val lastInstance = _instances.value.last()
                val instance = _instances.value[it]
                tree.search(lastInstance?.isbn!!)?.duplicates?.update(_instances.value.size - 1, it)
                tree.delete(isbn, it)
                instance?.dateF?.let { date ->
                    filterTree.search(lastInstance.dateF!!)?.duplicates?.update(_instances.value.size - 1, it)
                    filterTree.delete(date, it)
                }
                _instances.value[it] = _instances.value.last()
                _instances.value = _instances.value.copyOfRange(0, _instances.value.size - 1)
                LogsFile.writeln("Удаление экземпляра': $instance")
                count++
            }
            if (count == 0) throw NoSuchElementException("Нет таких экземпляров")
            else treeVersion.value++
            true
        }

    fun searchInstance(t: (String, Boolean) -> Unit) = withToastHandling(t) {
        getInstances(tree.search(instanceField.value)) ?: throw NoSuchElementException("Нет таких экземпляров")
    }

    fun getInstances(node: RedBlackTree<*>.Node?) =
        node?.duplicates?.mapNotNull { _instances.value[it] }

    fun saveInstances(t: (String, Boolean) -> Unit) = withToastHandling(t) {
        FileUtils.saveInstancesToFile(paths.second!!, _instances.value)
        LogsFile.writeln("Файл экземпляров сохранен")
        true
    }

    //filter functions
    fun filter(t: (String, Boolean) -> Unit) = withToastHandling(t) {
        val arr = mutableListOf<ReportField>()
        filterTree.filterRecursive(arr) { node, next, needCheck ->
            if (needCheck) {
                for (i in node.duplicates) {
                    try {
                        val instance = instances[i] ?: continue
                        val bookIndex = hashTable.get(instance.isbn)
                        val book = books[bookIndex!!] ?: continue
                        if (instance.inventoryNumber == filterInvNumField.value &&
                            book.author == filterAuthorField.value &&
                            instance.dateF >= filterFromField.value &&
                            instance.dateF <= filterToField.value
                        ) {
                            arr += ReportField(
                                instance.isbn,
                                book.title,
                                book.author,
                                instance.inventoryNumber,
                                instance.status,
                                instance.date
                            )
                        }
                    } catch (e: Exception) {
                        LogsFile.writeln("Нет связи между книгой и экземпляром: ${e.message}")
                    }
                }
            }
            val rightDateFlag = node.right?.key != null && node.right?.key!! <= filterToField.value
            val leftDateFlag = node.left?.key != null && node.left?.key!! >= filterFromField.value
            val n = when (next) {
                Check.All -> {
                    if (leftDateFlag && rightDateFlag) Check.All
                    else if (leftDateFlag) Check.LEFT
                    else if (rightDateFlag) Check.RIGHT
                    else Check.NONE
                }

                Check.LEFT -> if (leftDateFlag) Check.LEFT else Check.NONE
                Check.RIGHT -> if (rightDateFlag) Check.RIGHT else Check.NONE
                Check.NONE -> Check.NONE
            }
            n
        }
        _report.value = arr.toTypedArray()
        true
    }

    fun saveReport(t: (String, Boolean) -> Unit) = withToastHandling(t) {
        FileUtils.saveReportToFile(reportPath, _report.value)
        LogsFile.writeln("Файл экземпляров сохранен")
        true
    }

    val books get() = _books.value
    val instances get() = _instances.value
    val hashTable get() = _hashTable.value
    val rows get() = _rows.value
    val tree get() = _tree.value
    val filterTree get() = _filterTree.value
    val report get() = _report

}