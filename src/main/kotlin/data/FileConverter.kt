package data

import entities.Book
import entities.Instance
import java.io.File
import kotlin.text.trim

object FileConverter {

    fun initBooksFromFile(path: String): Array<Book?> {
        val array: MutableList<Book> = mutableListOf()
        File(path).bufferedReader().readLines().forEach { s ->
            try {
                val arr = s.split("|")
                val isbn = arr[0].trim()
                val title = arr[1].trim()
                val author = arr[2].trim()
                array.add(Book(isbn, title, author))
            } catch (e: Exception) {
                println("$s is not valid book")
            }
        }
        return array.toTypedArray()
    }

    fun initInstancesFromFile(path: String): Array<Instance?> {
        val array: MutableList<Instance?> = mutableListOf()
        File(path).bufferedReader().readLines().forEach { s ->
            try {
                val arr = s.split("|")
                val isbn = arr[0].trim()
                val invNum = arr[1].trim()
                val status = arr[2].trim()
                val date = arr[3].trim()
                array.add(Instance(isbn, invNum, status, date))
            } catch (e: Exception) {
                println("$s is not valid instance\nexception: $e")
            }
        }
        return array.toTypedArray()
    }

    fun saveBooksToFile(path: String, array: Array<Book?>) {
        File(path).printWriter().use { out ->
            array.forEach { it?.let { out.println("${it.isbn}|${it.title}|${it.author}") } }
        }
    }

    fun saveInstancesToFile(path: String, array: Array<Instance?>) {
        File(path).printWriter().use { out ->
            array.forEach { it?.let { out.println("${it.isbn}|${it.inventoryNumber}|${it.status}|${it.date}") } }
        }
    }
}