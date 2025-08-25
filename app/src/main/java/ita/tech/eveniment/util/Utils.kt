package ita.tech.eveniment.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale



fun setTimeZone( timer: Long, time_zone: String ): ZonedDateTime {
    return ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(timer),
        ZoneId.of(time_zone)
    )
}

fun formatTimeHora(time: ZonedDateTime?): String{
    val formato = DateTimeFormatter.ofPattern("HH:mm");
    return if (time != null) {
        time.format(formato)
    }else{
        ""
    }
}

fun formatTimeFechaEspaniol(time: ZonedDateTime?): String{
    val formato = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM", Locale("es", "ES"));
    return time?.format(formato)?.split(" ")?.mapIndexed { index, palabra ->
        when (palabra) {
            "de" -> palabra // Mantener "de" en minúscula
            else -> palabra.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } // Capitalizar otras palabras
        }
    }?.joinToString(" ") ?: ""
}

fun formatTimeFechaIngles(time: ZonedDateTime?): String{
    val formato = DateTimeFormatter.ofPattern("EEEE, dd MMMM", Locale.ENGLISH);
    return if (time != null) {
        time.format(formato)
    }else{
        ""
    }
}

fun stringDateToZoneDateTime( date: String, formatter: DateTimeFormatter, timeZone: String ): ZonedDateTime?{
    return if (date != "" ){
        ZonedDateTime.of(LocalDateTime.parse(date, formatter), ZoneId.of(timeZone))
    }else{
        null
    }
}