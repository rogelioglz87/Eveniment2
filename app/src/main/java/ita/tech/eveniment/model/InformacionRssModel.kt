package ita.tech.eveniment.model

import com.google.gson.annotations.SerializedName

data class InformacionRssModel(
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String
)

data class RssEntry(
    val noticias: Map<String, InformacionRssModel>
    // val noticias: List<InformacionRssModel>
)

data class RssResponse(
    // La respuesta de la API es un mapa de cadenas a objetos `RssItem`
    // val data: List<InformacionRssModel>
    val datos: Map<String, RssEntry>
    // val datos: List<RssEntry>
)