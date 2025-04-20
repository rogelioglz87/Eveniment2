package ita.tech.eveniment.state

data class CarrucelState(
    val duracionRecursoActual: Long = 0,
    val tipoSlide: String = "",
    val mostrarCarrucel: Boolean = true
)
