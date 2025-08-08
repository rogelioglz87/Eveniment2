package ita.tech.eveniment.data

import ita.tech.eveniment.model.InformacionPantallaModel
import ita.tech.eveniment.model.InformacionRecursoModel
import ita.tech.eveniment.model.RssResponse
import ita.tech.eveniment.util.Constants.Companion.ALTA_DISPOSITIVO
import ita.tech.eveniment.util.Constants.Companion.INFORMACION_PANTALLA
import ita.tech.eveniment.util.Constants.Companion.INFORMACION_RECURSOS
import ita.tech.eveniment.util.Constants.Companion.INFORMACION_RSS
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiEveniment {

    @FormUrlEncoded
    @POST(ALTA_DISPOSITIVO)
    suspend fun altaDispositivo(
        @Field("idPantalla") idPantalla: String,
        @Field("idCetroDefault") idCetroDefault: String
    ): Response<String>

    @FormUrlEncoded
    @POST(INFORMACION_PANTALLA)
    suspend fun obtenerInformacionPantalla(
        @Field("idPantalla") idPantalla: String
    ): Response<InformacionPantallaModel>

    @FormUrlEncoded
    @POST(INFORMACION_RECURSOS)
    suspend fun ontenerInformacionRecursos(
        @Field("idPantalla") idPantalla: String,
        @Field("tipo_consulta") tipoConsulta: String,
        @Field("id_lista_reproduccion") idListaReproduccion: Int
    ): Response<List<InformacionRecursoModel>>

    @FormUrlEncoded
    @POST(INFORMACION_RSS)
    suspend fun obtenerInformacionRss(
        @Field("idPantalla") idPantalla: String
    ): Response<RssResponse>

}