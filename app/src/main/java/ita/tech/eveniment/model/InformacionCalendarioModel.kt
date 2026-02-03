package ita.tech.eveniment.model

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken

data class InformacionCalendarioModel(
    val idProgramacion: Int = 0,
    val fechas: Map<String, CalendarioFechas>,
    val prioridad_horario: Int = 0,
    // val eventos: Map<String, InformacionRecursoModel>
    val eventos: JsonElement
)

fun InformacionCalendarioModel.extraerEventos(): List<InformacionRecursoModel> {
    val gson = Gson()
    return when {
        // Si PHP mandó índices correlativos [0,1,2] -> Array
        eventos.isJsonArray -> {
            val type = object : TypeToken<List<InformacionRecursoModel>>() {}.type
            gson.fromJson(eventos, type)
        }
        // Si PHP mandó índices rotos o asociativos {0:x, 2:y} -> Object
        eventos.isJsonObject -> {
            eventos.asJsonObject.entrySet().map { entry ->
                gson.fromJson(entry.value, InformacionRecursoModel::class.java)
            }
        }
        else -> emptyList()
    }
}

data class CalendarioFechas(
    val ini: String,
    val fin: String
)

data class CalendarioResponse(
    val datos: Map<String, InformacionCalendarioModel>
)


