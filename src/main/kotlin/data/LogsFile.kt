package data

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.Date

object LogsFile {
    private const val path = "src/main/resources/logs.txt"
    private val file = File(path)
    private val writer: BufferedWriter by lazy {
        file.parentFile.mkdirs()
        FileWriter(file, true).buffered()
    }

    fun writeln(message: String) {
        synchronized(this) {
            writer.write("Время ${Date()} $message: ")
            writer.newLine()
            writer.flush()
        }
    }

    fun cutHistory(maxLines: Int) {
        synchronized(this) {
            writer.flush()

            if (file.exists() && file.readLines().size > maxLines) {
                val lines = file.readLines()
                val newContent = lines.takeLast(maxLines).joinToString("\n")
                file.writeText(newContent + "\n")
            }
        }
    }

}