package data.repository

interface DefaultOperations<T>{

    fun add(t: T)

    fun delete(t: T)

    fun find(isbn: String): T
}