package data

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.Date

object LogsFile {
    private const val path = "src/main/resources/logs.txt"
    private val file = File(path)

    fun writeln(message: String) {
        file.appendText("Время ${Date()}: $message\n")
    }

    fun cutHistory(maxLines: Int) {
        if (file.exists() && file.readLines().size > maxLines) {
            val lines = file.readLines()
            val newContent = lines.takeLast(maxLines).joinToString("\n")
            file.appendText(newContent + "\n")
        }
    }

    fun clear() {
        if (file.exists()) {
            file.delete()
            file.createNewFile()
        }
    }
}