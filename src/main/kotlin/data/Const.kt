package data

import androidx.compose.runtime.mutableStateOf
import java.text.SimpleDateFormat
import java.util.Locale

internal val format = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
internal const val booksPath = "src/main/resources/books.txt"
internal const val instancesPath = "src/main/resources/instances.txt"
internal const val reportPath = "src/main/resources/report.txt"
internal const val countOfVisibleItemsInPicker = 5
internal const val itemHeight = 35f
internal const val listHeight = countOfVisibleItemsInPicker * itemHeight
val pathd = "src/main/resources/handbook_1_1.txt"
val pathe = "src/main/resources/handbook_1_2.txt"

internal val status = listOf(
    "в наличии", "выдана", "в читальном зале", "на реставрации",
    "утеряна", "списана", "зарезервирована", "в обработке",
    "на переплете", "в ремонте", "на проверке", "в цифровизации",
    "на выставке", "в хранилище", "временное отсутствие", "заказана",
    "в транспорте", "на списании", "повреждена", "на карантине"
)