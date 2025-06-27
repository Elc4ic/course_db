package structures

import entities.Book
import kotlin.math.abs
import kotlin.math.absoluteValue

class HashTable(val initSize: Int = 10, val resizeRationLow: Float = 0.25f, val resizeRationHigh: Float = 0.8f) {
    var size = initSize
    var occupancy = 0

    data class Entry(val key: String?, var value: Int?, var status: Boolean = false) {
        override fun toString(): String {
            return "Entry(key=$key, value=$value, status=$status)"
        }
    }

    private var table: Array<Entry> = Array(size) { Entry(null, null) }

    fun hash(key: String, j: Int = 0): Int {
        val hash = key.hashCode().absoluteValue
        return (hash + j) % size
    }

    fun put(author: String, index: Int) {
        put(author, index, true)
    }

    private fun put(author: String, bookIndex: Int, rehash: Boolean) {
        val key = author
        var i = 0
        var index: Int

        do {
            index = hash(key, i++)
        } while (table[index].status && i < size)

        table[index] = Entry(key, bookIndex, true)
        if (rehash) {
            occupancy++
            rehashIfNeeded()
        }
    }

    private fun rehashIfNeeded() {
        val ration: Float = occupancy.toFloat() / size.toFloat()
        when {
            ration >= resizeRationHigh -> resize(size * 2)
            ration <= resizeRationLow && size > initSize -> resize(size / 2)
        }
    }

    private fun resize(newSize: Int) {
        val oldEntries = table.filter { it.status }
        size = newSize
        table = Array(size) { Entry(null, null) }
        oldEntries.forEach { put(it.key!!, it.value!!, false) }
    }

    fun get(key: String): Int? {
        for (j in 0 until size) {
            val index = hash(key, j)
            val entry = table[index]
            if (!entry.status) break
            if (entry.key == key) return entry.value
        }
        return null
    }

    fun getIndex(key: String): Int? {
        for (j in 0 until size) {
            val index = hash(key, j)
            val entry = table[index]
            if (!entry.status) break
            if (entry.key == key) return index
        }
        return null
    }

    fun remove(key: String, value: Int): Boolean {
        for (j in 0 until size) {
            val index = hash(key, j)
            val entry = table[index]
            if (!entry.status) break
            if (entry.key == key && entry.value == value) {
                table[index] = Entry(null, null, false)
                occupancy--
                rehashIfNeeded()
                return true
            }
        }
        return false
    }

    fun initFromArray(array: Array<Book?>, keyAction: (Book) -> String) {
        clear()
        var lastProgress = -1
        var time = System.nanoTime()
        array.requireNoNulls().forEachIndexed { n, x ->
            val progress = (n * 100 / array.size)
            if (progress != lastProgress) {
                lastProgress = progress
                val progressBar = buildString {
                    append("[")
                    repeat(50) { i ->
                        append(if (i < progress / 2) "=" else " ")
                    }
                    append("] $progress%")
                    append(" XT_size = $size")
                }
                print("\r$progressBar")
            }
            put(keyAction(x), n)
        }
        println("\n Xt init completed in ${(System.nanoTime() - time) / 1000000} ms")
    }

    fun size() = table.filter { it.status }.size

    fun isEmpty() = size() == 0

    fun clear() {
        table.forEach { it.status = false }
        occupancy = 0
    }

    val rows get() = table

    override fun toString(): String {
        return table.joinToString("\n", "Hashtable {\n", "\n}") { "${it.key}=${it.value} ${it.status}" }
    }
}

