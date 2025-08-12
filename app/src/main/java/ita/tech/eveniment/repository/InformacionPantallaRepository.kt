package ita.tech.eveniment.repository

import ita.tech.eveniment.model.InformacionPantallaDB
import ita.tech.eveniment.room.InformacionPantallaDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class InformacionPantallaRepository @Inject constructor(private val informacionPantallaDatabaseDao: InformacionPantallaDatabaseDao) {

    suspend fun clear(concepto: String) = informacionPantallaDatabaseDao.clear(concepto)
    suspend fun insert(informacionPantallaDB: InformacionPantallaDB) = informacionPantallaDatabaseDao.insert(informacionPantallaDB)
    fun getInformacion(concepto: String): Flow<InformacionPantallaDB> = informacionPantallaDatabaseDao.getInformacion(concepto).flowOn(Dispatchers.IO).conflate()

}