package ita.tech.eveniment.model

data class RecursoDePlaylist(
    val path: String,
    val esVideo: Boolean,
    val duracion: Long = 0
)

data class RecursoNAS(
    val tipo_reproduccion: String,
    val data: Map<String, RecursoDePlaylist>
)