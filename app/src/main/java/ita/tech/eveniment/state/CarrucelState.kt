package ita.tech.eveniment.state

data class CarrucelState(
    val duracionRecursoActual: Long = 0,
    val tipoSlide: String = "",
    // Se manda a ProcesoViewModel
    // val mostrarCarrucel: Boolean = true
)
