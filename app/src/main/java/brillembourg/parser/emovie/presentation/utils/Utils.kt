package brillembourg.parser.emovie.presentation.utils

import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatter.ISO_DATE
import org.threeten.bp.temporal.TemporalAccessor

fun extractDateFromString(dateAsString: String): LocalDate {
    val dateFormatter: DateTimeFormatter = ISO_DATE
    return LocalDate.parse(dateAsString,dateFormatter)
}

fun extractYearFromDate(dateAsString: String): Int {
    return try {
        val dateFormatter: DateTimeFormatter = ISO_DATE
        val date: TemporalAccessor = dateFormatter.parse(dateAsString)
        val fmtOut: DateTimeFormatter =
            DateTimeFormatter.ofPattern("YYYY").withZone(ZoneOffset.UTC)
        return fmtOut.format(date).toInt()
    } catch (e: Exception) {
        Logger.error(e)
        0
    }
}