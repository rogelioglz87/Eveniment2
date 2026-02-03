package ita.tech.eveniment.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import ita.tech.eveniment.R
import ita.tech.eveniment.model.DatosAgenda
import ita.tech.eveniment.model.InformacionRecursoModel
import ita.tech.eveniment.model.RecursoDePlaylist
import ita.tech.eveniment.model.TextoAlarmaDB
import ita.tech.eveniment.repository.TextoAlarmaRepository
import org.json.JSONArray
import org.json.JSONObject
import java.io.DataOutputStream
import java.io.IOException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.collections.mutableListOf
import kotlin.random.Random
import kotlin.random.nextInt

private val random = Random
private val formatoHora = DateTimeFormatter.ofPattern("HH:mm")
private val formatoFechaEspaniol = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM", Locale("es", "ES"))
private val formatoFechaIngles = DateTimeFormatter.ofPattern("EEEE, dd MMMM", Locale.ENGLISH)
// Formato de fechas recibida
private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

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
    return if (time != null) {
        time.format(formatoHora)
    }else{
        ""
    }
}

fun formatTimeFechaEspaniol(time: ZonedDateTime?): String{
    return time?.format(formatoFechaEspaniol)?.split(" ")?.mapIndexed { index, palabra ->
        when (palabra) {
            "de" -> palabra // Mantener "de" en minúscula
            else -> palabra.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } // Capitalizar otras palabras
        }
    }?.joinToString(" ") ?: ""
}

fun formatTimeFechaIngles(time: ZonedDateTime?): String{
    return if (time != null) {
        time.format(formatoFechaIngles)
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

fun parseStringToObject( json: String ): JSONObject {
    val jObject = JSONObject(json)
    return jObject
}

fun parseObjectToArray( objeto: JSONObject, path: String ): List<RecursoDePlaylist>{
    val lista = mutableListOf<RecursoDePlaylist>()
    if( objeto.length() > 0 ){
        for( i in 0 until objeto.length() ){
            val datoObject = parseStringToObject(objeto.getString(i.toString()))
            val esVideo = datoObject.getString("tipo_archivo") == "video"
            lista.add(RecursoDePlaylist(
                path = "$path/${datoObject.getString("nombre")}",
                esVideo = esVideo,
                duracion = datoObject.getLong("duracion")
            ))
        }
    }
    return lista
}

fun obtener_path( path: String ): String{
    try {
        val uri = Uri.parse(path)
        val nombreCarpeta = uri.getQueryParameter("carpeta")
        if (nombreCarpeta.isNullOrEmpty()) {
            return ""
        }
        return "${uri.scheme}://${uri.host}/$nombreCarpeta"

    } catch (e: Exception) {
        return ""
    }
}

fun quitarDecimal( numero: String? ): Int{
    return numero?.toDoubleOrNull()?.toInt() ?: 0
}

fun formatearNombreImagen(ruta: String?): String {
    if (ruta.isNullOrEmpty()) return "day_113"
    return ruta.replace("/", "_").substringBeforeLast(".")
}

@SuppressLint("DiscouragedApi")
fun obtieneIdImagen(context: Context, ruta: String? ): Int {
    return context.resources.getIdentifier(ruta, "drawable", context.packageName)
}

@Composable
fun iconClima(
    nombreIcon: String,
    context: Context,
    modifier: Modifier = Modifier
){
    val nombreImagen = formatearNombreImagen(nombreIcon)
    val idImagen = obtieneIdImagen(context, nombreImagen)
    if(idImagen != 0){
        Image(
            painter = painterResource(id = idImagen),
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    }else{
        Image(
            painter = painterResource(id = R.drawable.day_113),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(0.7f),
            contentScale = ContentScale.Fit
        )
    }
}

fun obtenerDiaAbreviado(fechaStr: String): String {
    return try {
        val fecha = LocalDate.parse(fechaStr)
        val formatter = DateTimeFormatter.ofPattern("EEEE", Locale("es", "ES"))
        fecha.format(formatter)
            .lowercase() // Asegura que esté en minúsculas (ej. "miércoles")
            .take(3)     // Toma solo "mié"
    } catch (e: Exception) {
        // En caso de que la fecha venga mal o vacía
        "---"
    }
}

/**
 * Valida si se deben de mostrar o no los recursos de Texto
 * Reglas:
 * 1. Si la Fecha Inicio es MAYOR a la Fecha Actual, no mostrar, agendar para mostrar recurso.
 * 2. Si la Fecha Termino es MENOR a la Fecha Actual, no mostrar.
 * 3. Mostrar recurso si la Fecha Actual esta entre la Fecha Inicio y Fecha Termino, agendar para borrar recurso.
 * 4. Reagrupar recursos en caso de ser necesario
 */
suspend fun validaRecursosDeTexto(
    recursos: List<InformacionRecursoModel>,
    textoAgrupado: String = "si",
    timeZone: String,
    textoAlarmaRepository: TextoAlarmaRepository,
    context: Context
): List<InformacionRecursoModel>
{
    val nuevaListaRecursos = mutableListOf<InformacionRecursoModel>()
    val fechaActual = setTimeZone( System.currentTimeMillis(), timeZone )
    val listaEventosPermitidos = mutableListOf<DatosAgenda>()

    // Inicia proceso de validar recursos de Texto
    if( recursos.isNotEmpty() ){
        recursos.forEach { recurso ->
            if( recurso.tipo_slide == "texto" ){
                // Obtenemos la lista de los Eventos
                val eventos = recurso.obtenerDatosComoListaAgenda()
                if(eventos.isNotEmpty()){
                    eventos.forEachIndexed { index, evento ->

                        val valorAleatorio = obtenerValorAleatorio(1000,1999);
                        val idAlarma = "$index${valorAleatorio}".toInt()

                        val fechaInicio = stringDateToZoneDateTime(evento.fechas?.fecha_ini.toString(), formatter, timeZone)
                        val fechaTermino = stringDateToZoneDateTime(evento.fechas?.fecha_fin.toString(), formatter, timeZone)

                        // Evento MAYOR a la Fecha Actual
                        if(fechaInicio?.isAfter(fechaActual) ?: false){
                            // Crear alarma para mostrar el recurso (Va a ejecutar un metodo para validar de nuevo)
                            alarmaCalendario(context, fechaInicio, idAlarma, "TEXTO")
                            textoAlarmaRepository.insert(TextoAlarmaDB( alarmaId = idAlarma ))
                        }

                        // Obtenemos los recursos permitidos
                        else if( fechaActual.isAfter(fechaInicio) && fechaActual.isBefore(fechaTermino) ){
                            listaEventosPermitidos.add( evento )
                            // Crear alarma para borrar el recurso (Va a ejecutar un metodo para validar de nuevo)
                            alarmaCalendario(context, fechaTermino, idAlarma, "TEXTO")
                            textoAlarmaRepository.insert(TextoAlarmaDB( alarmaId = idAlarma ))
                        }
                    }
                }

                // Vaciamos la lista de Eventos
                recurso.eliminarDatoAgenda()
            }
        }
    }
    // Reorganizamos eventos de Texto
    if( recursos.isNotEmpty() ){
        recursos.forEach { recurso ->

            if( recurso.tipo_slide == "texto" && listaEventosPermitidos.isNotEmpty() ){
                if( textoAgrupado == "si" ){
                    // Agregar eventos permitidos
                    var contador = 0
                    for ( i in 0..2 ){
                        if( i in listaEventosPermitidos.indices ){
                            recurso.agregarDatoAgenda(listaEventosPermitidos[contador])
                            contador++
                        }
                    }

                    // Eliminamos eventos ya agregados (Al eliminar los valores el indice del arreglo se inicializa)
                    listaEventosPermitidos.subList(0, contador).clear() // Se coloca contador para que elimine los 3 primeros
                }else{
                    if( listaEventosPermitidos.isNotEmpty() ){
                        // Agrega evento permitido
                        recurso.agregarDatoAgenda(listaEventosPermitidos[0]) // Toma el primer elemento

                        // Eliminamos eventos ya agregados (Al eliminar los valores el indice del arreglo se inicializa)
                        listaEventosPermitidos.removeAt(0) // Elimina el primer elemento
                    }
                }

                // Guardamos el recurso
                nuevaListaRecursos.add(recurso)

            }else if( recurso.tipo_slide != "texto" ){
                nuevaListaRecursos.add(recurso)
            }
        }
    }
    return nuevaListaRecursos
}