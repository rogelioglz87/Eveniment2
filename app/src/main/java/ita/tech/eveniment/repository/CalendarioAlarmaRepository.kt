package ita.tech.eveniment.repository

import ita.tech.eveniment.model.CalendarioAlarmaDB
import ita.tech.eveniment.room.CalendarioAlarmaDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CalendarioAlarmaRepository @Inject constructor(private val calendarioAlarmaDatabaseDao: CalendarioAlarmaDatabaseDao) {

    suspend fun delete() = calendarioAlarmaDatabaseDao.delete()
    suspend fun insert(calendarioAlarmaDB: CalendarioAlarmaDB) = calendarioAlarmaDatabaseDao.insert(calendarioAlarmaDB)
    fun getInformacion(): Flow<List<CalendarioAlarmaDB>> = calendarioAlarmaDatabaseDao.getInformacion().flowOn(Dispatchers.IO).conflate()
}