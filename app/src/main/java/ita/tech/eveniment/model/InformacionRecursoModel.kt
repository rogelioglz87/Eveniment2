package ita.tech.eveniment.model

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.reflect.TypeToken

data class InformacionRecursoModel(

    val orden_evento: Int,
    // var datos: Any,
    var datos: JsonElement,
    val grupo: Int,
    val tipo_slide: String,
    val duracion: Int,
    val idPantalla: Int,
    val idEvento: Int,
    val tamanio: Long,
    val tipo_video_youtube: String,
    val pagina_powerbi: String,
    val prioridad: String,
    val fecha_ini: String,
    val fecha_fin: String,
    val recursos_nas: String,
    val id_reporte_powerbi: String
){
    // Opción A: Intentar obtenerlo como String simple
    fun obtenerDatosComoString(): String {
        return if (datos.isJsonPrimitive) {
            datos.asString // Devuelve el texto plano
        } else {
            "" // O devuelve el JSON crudo si prefieres: datos.toString()
        }
    }

    // Opción B: Intentar obtenerlo como la Lista Compleja
    fun obtenerDatosComoListaAgenda(): List<DatosAgenda> {
        val gson = Gson()

        // 1. Limpiamos el JSON sucio de PHP
        val datosLimpios = normalizarPhpArray(this.datos)

        // 2. Ahora que estamos seguros de que es un Array [ ... ]
        return try {
            if (datosLimpios.isJsonArray) {
                val type = object : TypeToken<List<DatosAgenda>>() {}.type
                gson.fromJson(datosLimpios, type)
            } else {
                // Caso de un solo objeto
                listOf(gson.fromJson(datosLimpios, DatosAgenda::class.java))
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun agregarDatoAgenda(nuevoDato: DatosAgenda) {
        val gson = Gson()
        // 1. Obtenemos la lista actual
        val listaActual = obtenerDatosComoListaAgenda().toMutableList()

        // 2. Agregamos el nuevo objeto
        listaActual.add(nuevoDato)

        // 3. Convertimos la lista de nuevo a JsonElement y actualizamos la variable
        this.datos = gson.toJsonTree(listaActual)
    }

    fun actualizarValorDatos( value: String ){
        datos = JsonPrimitive( value )
    }

    /**
     * Funcion que elimina todos los eventos de la lista, para volver a organizarlo
     */
    fun eliminarDatoAgenda() {
        val gson = Gson()
        this.datos = gson.toJsonTree(emptyList<DatosAgenda>())
    }
}

// 1. Estructura interna de los eventos
data class DetalleEvento(
    val evento: String?,
    val hora: String?,
    val desc: String?
)

// 2. Estructura de las fechas
data class FechasEvento(
    val fecha_ini: String?,
    val fecha_fin: String?
)

// 3. El objeto principal de la lista (Evento A, Evento B...)
data class DatosAgenda(
    val nombre: String?,
    val fechas: FechasEvento?,
    val eventos: List<DetalleEvento>?
)


fun normalizarPhpArray(elemento: JsonElement): JsonElement {
    // Si no es un objeto, no hay nada que normalizar
    if (!elemento.isJsonObject) return elemento

    val obj = elemento.asJsonObject
    // Verificamos si la primera llave es un número (ej: "0")
    val keys = obj.keySet()
    val esMapaDePhp = keys.isNotEmpty() && keys.all { it.toIntOrNull() != null }

    return if (esMapaDePhp) {
        val nuevoArray = JsonArray()
        // Ordenamos las llaves numéricamente para no perder el orden
        keys.map { it.toInt() }.sorted().forEach { key ->
            // Llamada recursiva para limpiar niveles internos
            nuevoArray.add(normalizarPhpArray(obj.get(key.toString())))
        }
        nuevoArray
    } else {
        // Si es un objeto normal (como 'fechas'), limpiamos sus hijos
        keys.forEach { key ->
            obj.add(key, normalizarPhpArray(obj.get(key)))
        }
        obj
    }
}