package ita.tech.eveniment.model

data class InformacionCalendarioModel(
    val idProgramacion: Int = 0,
    val fechas: Map<String, CalendarioFechas>,
    val prioridad_horario: Int = 0,
    val eventos: Map<String, InformacionRecursoModel>
)

data class CalendarioFechas(
    val ini: String,
    val fin: String
)

data class CalendarioResponse(
    val datos: Map<String, InformacionCalendarioModel>
)