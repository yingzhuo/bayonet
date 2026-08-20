import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun genTimestamp(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return formatter.format(LocalDateTime.now())
}
