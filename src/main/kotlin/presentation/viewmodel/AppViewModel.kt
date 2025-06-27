package presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import data.FileConverter
import data.booksPath
import data.instancesPath
import entities.Book
import entities.Instance
import entities.ReportField
import structures.HashTable
import structures.HashTable.Entry
import structures.RedBlackTree
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
    private val _tree = RedBlackTree()
    private val _filterTree = RedBlackTree()

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
    }

    private fun initFilterTree() {
        _filterTree.initFromArray(_instances.value) { it.date }
    }

    //books functions
    fun addBook(book: Book) {
        _books.value += book
        _hashTable.value.put(book.isbn, _books.value.size - 1)
        _rows.value = _hashTable.value.rows.clone()
    }

    fun deleteBook() {
        _hashTable.value.get(bookField.value)?.let {
            _books.value[it] = null
            _hashTable.value.remove(bookField.value, it)
            _rows.value = _hashTable.value.rows.clone()
        }
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

    fun deleteInstance() {
        val index = _tree.search(instanceField.value)?.duplicates?.first()
        index?.let {
            _instances.value[index] = null
            _hashTable.value.remove(instanceField.value, it)
        }

    }

    fun searchInstance(): List<Instance> {
        return getInstances(_tree.search(instanceField.value))
    }

    fun getInstances(node: Node?): List<Instance> {
        return node?.duplicates?.mapNotNull { _instances.value[it] } ?: emptyList()
    }

    fun saveInstances() {
        FileConverter.saveInstancesToFile(instancesPath, _instances.value)
    }

    //filter functions
    fun filter() {
        _report.value = _filterTree.filterRecursive { node ->
            val arr = Array<ReportField?>(node.duplicates.size) { null }

            for (i in node.duplicates) {
                try {
                    var index = 0
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
                        arr[index] = ReportField(
                            instance.isbn,
                            book.title,
                            book.author,
                            instance.inventoryNumber,
                            instance.status,
                            instance.date
                        )
                        index++
                    }
                } catch (e: Exception) {
                    println("Нет связи между книгой и экземпляром")
                }
            }
            arr
        }

    }

    val books get() = _books
    val instances get() = _instances
    val hashTable get() = _hashTable
    val rows get() = _rows
    val tree get() = _tree
    val filterTree get() = _filterTree
    val report get() = _report

}