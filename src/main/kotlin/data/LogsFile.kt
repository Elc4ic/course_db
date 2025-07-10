package data

import java.io.File
import java.util.Date

object LogsFile {
    const val path = "src/main/resources/logs.txt"
    val file = File(path)

    fun write(message: String) {
        file.createNewFile()
        file.appendText("Время: ${Date()} $message\n")
    }

    fun writeln(message: String) {
        file.createNewFile()
        file.appendText("Время: ${Date()} $message\n")
    }
}