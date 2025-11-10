package ita.tech.eveniment.model

data class InformacionRecursoModel(

    val orden_evento: Int,
    var datos: Any,
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
    val recursos_nas: String
)
