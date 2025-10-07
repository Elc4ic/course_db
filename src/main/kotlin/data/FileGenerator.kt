package data

import io.github.serpro69.kfaker.Faker
import io.github.serpro69.kfaker.fakerConfig
import java.io.File
import java.time.LocalDate
import kotlin.random.Random

class FileGenerator() {
    val bookFile = File("src/main/resources/books.txt")
    val instanceFile = File("src/main/resources/instances.txt")
    val booksISBNs = mutableListOf<String>()

    private val adjectives = listOf(
        "Тайный", "Потерянный", "Последний", "Темный", "Скрытый",
        "Великий", "Забытый", "Древний", "Легендарный", "Запретный",
        "Мистический", "Кровавый", "Священный", "Проклятый", "Бесконечный",
        "Исчезнувший", "Серебряный", "Золотой", "Хрустальный", "Обреченный",
        "Вечный", "Сквозной", "Ледяной", "Огненный", "Лунный",
        "Солнечный", "Звездный", "Призрачный", "Демонический", "Ангельский",
        "Изумрудный", "Рубиновый", "Сапфировый", "Багровый", "Безмолвный",
        "Загадочный", "Непостижимый", "Невидимый", "Пограничный", "Пропавший",
        "Воскресший", "Зачарованный", "Окаменелый", "Искаженный", "Разрушенный",
        "Спящий", "Пробуждающийся", "Исчезающий", "Безымянный", "Неупокоенный"
    )

    private val nouns = listOf(
        "замок", "ключ", "артефакт", "город", "остров",
        "кодекс", "архив", "свиток", "алтарь", "пророчество",
        "меч", "кольцо", "диск", "кубок", "кристалл",
        "зеркало", "портал", "лабиринт", "храм", "собор",
        "легенда", "тайна", "заговор", "клятва", "проклятие",
        "наследие", "откровение", "знание", "секрет", "договор",
        "королевство", "империя", "династия", "революция", "война",
        "путешествие", "экспедиция", "исследование", "открытие", "изобретение",
        "призрак", "демон", "ангел", "дракон", "единорог",
        "феникс", "вампир", "оборотень", "русалка", "голем",
        "сад", "лес", "гора", "река", "пустыня",
        "бездна", "преисподняя", "рай", "чистилище", "космос"
    )

    val months = mapOf(
        1 to "Jan",
        2 to "Feb",
        3 to "Mar",
        4 to "Apr",
        5 to "May",
        6 to "Jun",
        7 to "Jul",
        8 to "Aug",
        9 to "Sep",
        10 to "Oct",
        11 to "Nov",
        12 to "Dec"
    )

    fun generateISBN(): String {
        val prefix = "978"
        val group = Random.nextInt(0, 10).toString()
        val publisher = Random.nextInt(100, 999).toString()
        val title = Random.nextInt(10000, 99999).toString()

        val partialISBN = prefix + group + publisher + title
        val checkDigit = calculateCheckDigit(partialISBN)

        return "$prefix-$group-$publisher-$title-$checkDigit"
    }

    private fun calculateCheckDigit(partialISBN: String): Int {
        var sum = 0
        for ((index, char) in partialISBN.withIndex()) {
            val digit = char.toString().toInt()
            sum += if (index % 2 == 0) digit * 1 else digit * 3
        }
        return (10 - (sum % 10)) % 10
    }

    fun generateTitle(): String {
        val adjective = adjectives.random()
        val noun = nouns.random()

        return when (Random.nextInt(0, 3)) {
            0 -> "$adjective $noun"
            1 -> "$adjective $noun ${nouns.random()}"
            else -> "$adjective ${nouns.random()} ${nouns.random()}"
        }
    }

    fun generateDate(): String {
        val month = Random.nextInt(1, 12)
        val year = Random.nextInt(1970, 2025)
        val day = Random.nextInt(1, LocalDate.of(year, month, 1).lengthOfMonth())
        return "$day ${months[month]} $year"
    }

    fun fillBookFileRandomly(size: Int) {
        booksISBNs.clear()
        val config = fakerConfig { locale = "ru" }
        val faker = Faker(config)
        val isNewFileCreated = bookFile.createNewFile()
        if (isNewFileCreated) {
            for (i in 1..size) {
                val isbn = generateISBN()
                booksISBNs.add(isbn)
                val title = generateTitle()
                val author = faker.name.nameWithMiddle()
                bookFile.appendText("$isbn|$title|$author\n")
            }
        }
    }

    fun fillInstancesFileRandomly(size: Int) {
        val isNewFileCreated = instanceFile.createNewFile()
        if (isNewFileCreated) {
            for (i in 1..size) {
                val isbn = booksISBNs.random()
                val invNum = (1..10).random()
                val status = status.random()
                val date = generateDate()
                instanceFile.appendText("$isbn|$invNum|$status|$date\n")
            }
        }
    }

    fun generateFiles(size: Int) {
        bookFile.delete()
        instanceFile.delete()
        fillBookFileRandomly(size)
        fillInstancesFileRandomly(size)
    }
}


fun main() {
    val fileGenerator = FileGenerator()
    fileGenerator.generateFiles(6)
}