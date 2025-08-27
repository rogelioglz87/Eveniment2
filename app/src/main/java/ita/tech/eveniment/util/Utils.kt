package ita.tech.eveniment.util

import java.io.DataOutputStream
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.random.Random
import kotlin.random.nextInt

private val random = Random

/**
 * Obtiene un numero aleatorio de un rango dado
 */
fun obtenerValorAleatorio( inicio: Int, fin: Int ): Int{
    return random.nextInt(inicio..fin)
}

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

fun ejecutarComando( comando:String ): String {
    try {
        // 1. Inicia un proceso de superusuario ('su') de forma interactiva
        val process = Runtime.getRuntime().exec("su")
        val reader = process.inputStream.bufferedReader()
        val output = reader.readText()

        // 2. Obtenemos un canal para escribir comandos en el proceso
        val os = DataOutputStream(process.outputStream)

        // 3. Escribimos nuestro comando y simulamos un "Enter" con '\n'
        os.writeBytes("$comando\n")
        os.flush()

        // 4. Escribimos el comando 'exit' para cerrar la shell de root limpiamente
        os.writeBytes("exit\n")
        os.flush()

        // 5. Esperamos a que el proceso y sus comandos terminen
        process.waitFor()
        return output.toString()
    }catch (e: IOException){
        e.printStackTrace()
        return ""
    }
}