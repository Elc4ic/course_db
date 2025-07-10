package presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import data.FileConverter
import data.LogsFile
import data.booksPath
import data.format
import data.instancesPath
import data.reportPath
import entities.Book
import entities.Instance
import entities.ReportField
import structures.HashTable
import structures.HashTable.Entry
import structures.RedBlackTree
import structures.RedBlackTree.Check
import structures.RedBlackTree.Node
import java.util.Date


class AppViewModel {
    private val _books = mutableStateOf(FileConverter.initBooksFromFile(booksPath))
    private val _instances = mutableStateOf(FileConverter.initInstancesFromFile(instancesPath))
    private val _report = mutableStateOf(arrayOf<ReportField>())

    val bookField = mutableStateOf("")
    val instanceField = mutableStateOf("")
    val filterAuthorField = mutableStateOf("")
    val filterInvNumField = mutableStateOf("")
    val filterFromField = mutableStateOf(Date())
    val filterToField = mutableStateOf(Date())

    private val _hashTable = mutableStateOf(HashTable(_books.value.size))
    private val _rows = mutableStateOf(arrayOf<Entry>())
    private val _tree = RedBlackTree<String>()
    private val _filterTree = RedBlackTree<Date>()

    init {
        initHashTable()
        initTree()
        initFilterTree()
    }

    private fun initHashTable() {
        _hashTable.value.clear()
        _hashTable.value.initFromArray(_books.value) { it.isbn }
        _rows.value = _hashTable.value.rows
    }

    private fun initTree() {
        _tree.initFromArray(_instances.value) { it.isbn }
        _tree.print()
    }

    private fun initFilterTree() {
        _filterTree.initFromArray(_instances.value) { it.dateF }
        _filterTree.print()
    }

    //books functions
    fun addBook(book: Book) {
        _books.value += book
        _hashTable.value.put(book.isbn, _books.value.size - 1)
        _rows.value = _hashTable.value.rows.clone()
    }

    fun deleteBook(isbn: String, title: String, author: String) {
        val index = _hashTable.value.get(isbn) ?: return
        val book = _books.value[index] ?: return
        if (book.title != title || book.author != author) return
        LogsFile.writeln("Удаление книги: $book")
        _books.value[index] = _books.value.last()
        _books.value.copyOfRange(0, _books.value.size - 2)
        _hashTable.value.remove(bookField.value, index)
        _tree.search(isbn)?.duplicates?.forEach { ni ->
            val instance = _instances.value[ni]
            _tree.delete(isbn, ni)
            instance?.dateF?.let { _filterTree.delete(it, ni) }
            _instances.value[ni] = null
        }
        _instances.value = _instances.value.filter { it != null }.toTypedArray()
        _rows.value = _hashTable.value.rows.clone()
    }

    fun searchBook(): Book? {
        return _hashTable.value.get(bookField.value)?.let { _books.value[it] }
    }

    fun saveBooks() {
        FileConverter.saveBooksToFile(booksPath, _books.value)
    }

    //instances functions
    fun addInstance(instance: Instance) {
        _instances.value += instance
        _tree.add(instance.isbn, _books.value.size - 1)
    }

    fun deleteInstance(isbn: String, invNum: String) {
        val index = _tree.search(isbn)?.duplicates?.first {
            invNum == _instances.value[it]?.inventoryNumber
        }
        index?.let {
            val instance = _instances.value[index]
            _instances.value[index] = _instances.value.last()
            _instances.value.copyOfRange(0, _instances.value.size - 2)
            _tree.delete(isbn, it)
            instance?.dateF?.let { _filterTree.delete(it, index) }
        }
        _tree.print()
    }

    fun searchInstance(): List<Instance> {
        return getInstances(_tree.search(instanceField.value))
    }

    fun getInstances(node: RedBlackTree<*>.Node?): List<Instance> {
        return node?.duplicates?.mapNotNull { _instances.value[it] } ?: emptyList()
    }

    fun saveInstances() {
        FileConverter.saveInstancesToFile(instancesPath, _instances.value)
    }

    //filter functions
    fun filter() {
        _filterTree.print()
        val arr = mutableListOf<ReportField>()
        _filterTree.filterRecursive(arr) { node, next, needCheck ->
            if (needCheck) {
                for (i in node.duplicates) {
                    try {
                        val instance = _instances.value[i]
                        if (instance == null) continue
                        val bookIndex = _hashTable.value.get(instance.isbn)
                        val book = _books.value[bookIndex!!]
                        if (book == null) continue
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
            if (node.right?.key != null && node.left?.key != null) {
                println("$n ${node.left?.key} ${node.right?.key}")
            }
            n
        }
        _report.value = arr.toTypedArray()
    }

    fun saveReport() {
        FileConverter.saveReportToFile(reportPath, _report.value)
    }

    val books get() = _books
    val instances get() = _instances
    val hashTable get() = _hashTable
    val rows get() = _rows
    val tree get() = _tree
    val filterTree get() = _filterTree
    val report get() = _report

}