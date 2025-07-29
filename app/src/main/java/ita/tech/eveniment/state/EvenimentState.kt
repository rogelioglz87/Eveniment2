package ita.tech.eveniment.state

import androidx.compose.ui.graphics.Color

data class EvenimentState(
    val idDispositivo: String = "",
    val ipAddress: String = "",
    val ipVPN: String = "",
    val altaDispositivo: Boolean = false,
    val estatusInternet: Boolean = false,

    val totalRecursos: Int = 0,
    val totalRecursosDescargados: Int = 0,

    //-- Validaciones del proceso
    val bandCarpetasCreadas: Boolean = false,
    val bandInicioDescarga: Boolean = false,  // Indica el momento en que inicia la descarga de los recursos
    val bandDescargaRecursos: Boolean = true, // Indica el momento en que se debe quitar la pantalla de descarga
    val bandDescargaLbl: Boolean = false,     // Indicara en una etiqueta en pantalla cuando se realiza una descarga de recursos

    //-- Colores de la Pantalla
    val color_primario: Color = Color(0xFFFFFFFF),
    val color_secundario: Color = Color(0xFFFFFFFF),
    val color_boton: Color = Color(0xFFFFFFFF),
    val color_texto: Color = Color(0xFFFFFFFF),
    val color_logo: Color = Color(0xFFFFFFFF),
    val u_color_primario: Color = Color(0xFFFFFFFF),
    val u_color_secundario: Color = Color(0xFFFFFFFF),
    val u_color_texto: Color = Color(0xFFFFFFFF),
    val u_color_logo: Color = Color(0xFFFFFFFF)

)
