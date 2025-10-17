package presentation.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import data.FileUtils
import data.LogsFile
import data.reportPath
import entities.Book
import entities.Instance
import entities.ReportField
import structures.HashTable
import structures.HashTable.Entry
import structures.RedBlackTree
import structures.RedBlackTree.Check
import java.util.Date


class AppViewModel {
    private lateinit var paths: Pair<String?, String?>
    private lateinit var _books: MutableState<Array<Book?>>
    private lateinit var _instances: MutableState<Array<Instance?>>
    private val _report = mutableStateOf(arrayOf<ReportField>())

    val bookField = mutableStateOf("")
    val instanceField = mutableStateOf("")
    val filterAuthorField = mutableStateOf("")
    val filterInvNumField = mutableStateOf("")
    val filterFromField = mutableStateOf(Date())
    val filterToField = mutableStateOf(Date())

    private val _hashTable = mutableStateOf(HashTable())
    private val _rows = mutableStateOf(arrayOf<Entry>())
    private val _tree = mutableStateOf(RedBlackTree<String>())
    private val _treeList = mutableStateOf(listOf<String>())
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
        _hashTable.value.clear()
        _hashTable.value.initFromArray(_books.value) { it.isbn }
        _rows.value = _hashTable.value.rows
        LogsFile.writeln(_hashTable.value.toString())
    }

    private fun initTree() {
        _tree.value.initFromArray(_instances.value) { it.isbn }
        _treeList.value = _tree.value.toString().split("\n")
        LogsFile.writeln(_tree.value.toString())
    }

    private fun initFilterTree() {
        _filterTree.value.initFromArray(_instances.value) { it.dateF }
        LogsFile.writeln(_filterTree.value.toString())
    }

    //books functions
    fun addBook(book: Book) {
        _books.value += book
        _hashTable.value.put(book.isbn, _books.value.size - 1)
        _rows.value = _hashTable.value.rows.clone()
        LogsFile.writeln("Книга добавленна: $book")
    }

    fun deleteBook(isbn: String, title: String, author: String) {
        val index = _hashTable.value.get(isbn) ?: return
        val book = _books.value[index] ?: return
        if (book.title != title || book.author != author) return

        val lastBook = _books.value.last()
        _hashTable.value.rows.first { it.key == lastBook?.isbn }.value = index
        _hashTable.value.remove(book.isbn, index)
        _books.value[index] = lastBook
        _books.value = _books.value.copyOfRange(0, _books.value.size - 1)
        LogsFile.writeln("Удаление книги: $book")
        deleteInTreeByBook(isbn)

        _rows.value = _hashTable.value.rows
    }

    fun deleteInTreeByBook(isbn: String) {
        _tree.value.search(isbn)?.duplicates?.forEach { ni ->
            val lastInstance = _instances.value.last()
            val instance = _instances.value[ni]
            LogsFile.writeln("Также удаляется экземпляр: $instance")
            _tree.value.search(lastInstance?.isbn!!)?.duplicates?.update(_instances.value.size - 1, ni)
            _tree.value.delete(isbn, ni)
            _instances.value[ni] = _instances.value.last()
            _instances.value = _instances.value.copyOfRange(0, _instances.value.size - 1)
            instance?.dateF?.let { _filterTree.value.delete(it, ni) }
        }
        _treeList.value = _tree.value.toString().split("\n")
    }

    fun searchBook(): Book? {
        val book = _hashTable.value.get(bookField.value)?.let { _books.value[it] }
        LogsFile.writeln("Найдена книга: $book")
        return book
    }

    fun saveBooks() {
        FileUtils.saveBooksToFile(paths.first!!, _books.value)
        LogsFile.writeln("Файл книг обновлен")
    }

    //instances functions
    fun addInstance(instance: Instance) {
        _instances.value += instance
        _tree.value.add(instance.isbn, _books.value.size - 1)
        LogsFile.writeln("Экземпляр добавлен:${instance}")
    }

    fun deleteInstance(isbn: String, invNum: String) {
        val index = _tree.value.search(isbn)?.duplicates?.first {
            invNum == _instances.value[it]?.inventoryNumber
        }
        index?.let {
            val instance = _instances.value[index]
            _instances.value[index] = _instances.value.last()
            _instances.value.copyOfRange(0, _instances.value.size - 2)
            _tree.value.delete(isbn, it)
            instance?.dateF?.let { _filterTree.value.delete(it, index) }
            LogsFile.writeln("Удаление экземпляра': $instance")
        }
    }

    fun searchInstance(): List<Instance> {
        return getInstances(_tree.value.search(instanceField.value))
    }

    fun getInstances(node: RedBlackTree<*>.Node?): List<Instance> {
        return node?.duplicates?.mapNotNull { _instances.value[it] } ?: emptyList()
    }

    fun saveInstances() {
        FileUtils.saveInstancesToFile(paths.second!!, _instances.value)
        LogsFile.writeln("Файл экземпляров сохранен")
    }

    //filter functions
    fun filter() {
        val arr = mutableListOf<ReportField>()
        _filterTree.value.filterRecursive(arr) { node, next, needCheck ->
            if (needCheck) {
                for (i in node.duplicates) {
                    try {
                        val instance = _instances.value[i] ?: continue
                        val bookIndex = _hashTable.value.get(instance.isbn)
                        val book = _books.value[bookIndex!!] ?: continue
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
            var n = when (next) {
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
    }

    fun saveReport() {
        FileUtils.saveReportToFile(reportPath, _report.value)
        LogsFile.writeln("Файл экземпляров сохранен")
    }

    val books get() = _books.value
    val instances get() = _instances.value
    val hashTable get() = _hashTable.value
    val rows get() = _rows.value
    val tree get() = _tree.value
    val treeList get() = _treeList.value
    val filterTree get() = _filterTree.value
    val report get() = _report

}