package brillembourg.parser.emovie.data.local_imp

import androidx.room.TypeConverter
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

object DateConverter {

    @TypeConverter
    fun toDate(epochSeconds: Long): LocalDate {
        return Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    @TypeConverter
    fun fromDate(date: LocalDate): Long {
        val zoneId: ZoneId = ZoneId.systemDefault()
        return date.atStartOfDay(zoneId).toEpochSecond()
    }
}