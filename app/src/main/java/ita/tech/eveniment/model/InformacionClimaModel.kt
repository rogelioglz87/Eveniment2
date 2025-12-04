package ita.tech.eveniment.model

data class InformacionClimaModel(
    val clima_actual: ClimaActual,
    val clima_dias_siguientes: List<ClimaSiguiente>
)

data class ClimaActual(
    val temp_c: Double,
    val condition: CondicionDia,
    val uv: Double
)

data class CondicionDia(
    val text: String,
    val icon: String,
    val code: Int
)

data class ClimaSiguiente(
    val date: String,
    val day: DiaClima
)

data class DiaClima(
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val avgtemp_c: Double,
    val condition: CondicionDia,
    val uv: Double
)