package ita.tech.eveniment.repository

import ita.tech.eveniment.model.TextoAlarmaDB
import ita.tech.eveniment.room.TextoAlarmaDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class TextoAlarmaRepository @Inject constructor(private val textoAlarmaDatabaseDao: TextoAlarmaDatabaseDao) {

    suspend fun delete() = textoAlarmaDatabaseDao.delete()
    suspend fun insert(textoAlarmaDB: TextoAlarmaDB) = textoAlarmaDatabaseDao.insert(textoAlarmaDB)
    fun getInformacion(): Flow<List<TextoAlarmaDB>> = textoAlarmaDatabaseDao.getInformacion().flowOn(Dispatchers.IO).conflate()

}