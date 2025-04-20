package ita.tech.eveniment.repository

import ita.tech.eveniment.data.ApiEveniment
import ita.tech.eveniment.model.InformacionPantallaModel
import ita.tech.eveniment.model.InformacionRecursoModel
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

    suspend fun obtenerInformacionRecursos(idPantalla: String, tipoConsulta: String): List<InformacionRecursoModel>? {
        val response = apiEveniment.ontenerInformacionRecursos(idPantalla, tipoConsulta)
        if( response.isSuccessful ){
            return response.body()
        }
        return null
    }

}