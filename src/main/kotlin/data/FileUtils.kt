package data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import entities.Book
import entities.Instance
import entities.ReportField
import java.io.File
import kotlin.io.path.createTempFile
import kotlin.io.path.writeLines
import kotlin.text.trim

object FileUtils {
    var _booksPath: MutableState<String?> = mutableStateOf(null)
    var _instancesPath: MutableState<String?> = mutableStateOf(null)
    val pathsFile = File(pathsFilePath)
    val actualBooksStore = "actualBooksStore:"
    val recentBooksStore = "recentBooksStore:"
    val actualInstanceStore = "actualInstanceStore:"
    val recentInstanceStore = "recentInstanceStore:"

    fun setPaths(booksPath: String, instancePath: String) {
        outdatePaths()
        setBookPath(booksPath)
        setInstancePath(instancePath)
    }

    fun setBookPath(booksPath: String) {
        pathsFile.appendText("$actualBooksStore$booksPath\n")
        _booksPath.value = booksPath
    }

    fun setInstancePath(instancePath: String) {
        pathsFile.appendText("$actualInstanceStore$instancePath\n")
        _instancesPath.value = instancePath
    }

    fun getPaths(): Pair<String?, String?> {
        val booksStorePath = getPath(actualBooksStore)
        val instanceStorePath = getPath(actualInstanceStore)
        return booksStorePath to instanceStorePath
    }

    fun getPath(prefix: String): String? {
        pathsFile.bufferedReader().readLines().forEach {
            try {
                if (it.startsWith(prefix)) {
                    return it.substring(prefix.length, it.length)
                }
            } catch (e: Exception) {
                LogsFile.writeln("no file for this storage: $e")
            }
        }
        return null
    }

    fun outdatePaths() {
//        val lines = pathsFile.readLines()
//        val arr = lines.map { line ->
//            if (line.startsWith(actualInstanceStore)) "$recentInstanceStore${
//                line.substring(
//                    actualInstanceStore.length
//                )
//            }\n"
//            else if (line.startsWith(actualBooksStore)) "$recentBooksStore${
//                line.substring(
//                    actualBooksStore.length
//                )
//            }\n"
//            else line
//        }
        pathsFile.delete()
        pathsFile.createNewFile()
//        pathsFile.appendText(arr.joinToString(""))
    }

    fun initBooksFromFile(path: String): Array<Book?> {
        val array: MutableList<Book> = mutableListOf()
        File(path).forEachLine { s ->
            try {
                val arr = s.split("|")
                val isbn = arr[0].trim()
                val title = arr[1].trim()
                val author = arr[2].trim()
                array.add(Book(isbn, title, author))
            } catch (e: Exception) {
                LogsFile.writeln("not valid book in file: $s")
            }
        }
        return array.toTypedArray()
    }

    fun initInstancesFromFile(path: String): Array<Instance?> {
        val array: MutableList<Instance?> = mutableListOf()
        File(path).forEachLine { s ->
            try {
                val arr = s.split("|")
                val isbn = arr[0].trim()
                val invNum = arr[1].trim()
                val status = arr[2].trim()
                val date = arr[3].trim()
                array.add(Instance(isbn, invNum, status, date))
            } catch (e: Exception) {
                LogsFile.writeln("not valid instance in file: $s")
            }
        }
        return array.toTypedArray()
    }

    fun saveBooksToFile(path: String, array: Array<Book?>) {
        val file = File(path)
        file.delete()
        file.createNewFile()
        file.printWriter().use { out ->
            array.forEach { it?.let { out.println("${it.isbn}|${it.title}|${it.author}") } }
        }
    }

    fun addBooksToFile(path: String) {
        val books = initBooksFromFile(path)
        val pathOld = getPaths().first!!
        val file = File(pathOld)
        val l = file.readLines()
        file.printWriter().use { out ->
            l.forEach { out.println(it) }
            books.forEach {
                it?.let {
                    val str = "${it.isbn}|${it.title}|${it.author}"
                    if (l.count { s -> s.contains(it.isbn) } == 0) {
                        out.println(str)
                    } else {
                        LogsFile.writeln("Книга дубликат в добавочном файле: $str")
                    }
                }
            }
        }
    }

    fun saveInstancesToFile(path: String, array: Array<Instance?>) {
        val file = File(path)
        file.delete()
        file.createNewFile()
        file.printWriter().use { out ->
            array.forEach {
                it?.let { out.println("${it.isbn}|${it.inventoryNumber}|${it.status}|${it.date}") }
            }
        }
    }

    fun addInstancesToFile(path: String) {
        val instance = initInstancesFromFile(path)
        val pathOld = getPaths().second!!
        val file = File(pathOld)
        val l = file.readLines()
        file.printWriter().use { out ->
            l.forEach { out.println(it) }
            instance.forEach {
                it?.let {
                    val str = "${it.isbn}|${it.inventoryNumber}|${it.status}|${it.date}"
                    if (l.count { s -> s == str } == 0) {
                        out.println(str)
                    } else {
                        LogsFile.writeln("Экземпляр дубликат в добавочном файле: $str")
                    }
                }
            }
        }
    }

    fun saveReportToFile(path: String, array: Array<ReportField>) {
        val file = File(path)
        file.delete()
        file.createNewFile()
        file.printWriter().use { out ->
            array.forEach { out.println("${it.isbn} ${it.title} ${it.author} ${it.invNum} ${it.status} ${it.date}") }
        }
    }
}