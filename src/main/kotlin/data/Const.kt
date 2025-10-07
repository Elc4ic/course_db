package data

import java.text.SimpleDateFormat
import java.util.Locale

internal val format = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
internal const val logsPath = "src/main/resources/logs.txt"
internal const val pathsFilePath = "src/main/resources/paths.txt"
internal const val reportPath = "src/main/resources/report.txt"

internal val status = listOf(
    "в наличии", "выдана", "в читальном зале", "на реставрации",
    "утеряна", "списана", "зарезервирована", "в обработке",
    "на переплете", "в ремонте", "на проверке", "в цифровизации",
    "на выставке", "в хранилище", "временное отсутствие", "заказана",
    "в транспорте", "на списании", "повреждена", "на карантине"
)