package ita.tech.eveniment.repository

import ita.tech.eveniment.data.ApiEveniment
import ita.tech.eveniment.model.InformacionPantallaModel
import ita.tech.eveniment.model.InformacionCalendarioModel
import ita.tech.eveniment.model.InformacionClimaModel
import ita.tech.eveniment.model.InformacionRecursoModel
import ita.tech.eveniment.model.RssEntry
import javax.inject.Inject

class EvenimentRepository @Inject constructor(private val apiEveniment: ApiEveniment) {

    suspend fun altaDispositivo(idPantalla: String, idCetroDefault: String): String?{
        val response = apiEveniment.altaDispositivo(idPantalla, idCetroDefault)
        if(response.isSuccessful){
            return response.body()
        }
        return null
    }

    suspend fun obtenerInformacionPantalla(idPantalla: String): InformacionPantallaModel? {
        val response = apiEveniment.obtenerInformacionPantalla(idPantalla)
        if( response.isSuccessful ){
            return response.body()
        }
        return null
    }

    suspend fun obtenerInformacionRecursos(idPantalla: String, tipoConsulta: String, idListaReproduccion: Int): List<InformacionRecursoModel>? {
        val response = apiEveniment.obtenerInformacionRecursos(idPantalla, tipoConsulta, idListaReproduccion)
        if( response.isSuccessful ){
            return response.body()
        }
        return null
    }

    suspend fun obtenerInformacionRecursosCalendario(idPantalla: String, tipoConsulta: String): List<InformacionCalendarioModel>? {
        val response = apiEveniment.obtenerInformacionRecursosCalendario(idPantalla, tipoConsulta)
        if( response.isSuccessful ){
            return response.body()?.datos?.values?.toList() ?: emptyList()
        }
        return null
    }

    suspend fun obtenerInformacionRss(idPantalla: String): List<RssEntry>? {
        val response = apiEveniment.obtenerInformacionRss(idPantalla)
        if( response.isSuccessful ){
            return response.body()?.datos?.values?.toList() ?: emptyList()
        }
        return null
    }

    suspend fun obtenerInformacionClima(idPantalla: String): InformacionClimaModel?{
        val response = apiEveniment.obtenerInformacionClima(idPantalla)
        if( response.isSuccessful ){
            return response.body()
        }
        return null
    }

}