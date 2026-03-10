package ita.tech.eveniment.repository

import ita.tech.eveniment.model.ReporteTokenPBIDB
import ita.tech.eveniment.room.ReporteTokenPBIDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ReporteTokenPBIRepository @Inject constructor(private val reporteTokenPBIDatabaseDao: ReporteTokenPBIDatabaseDao) {

    suspend fun delete() = reporteTokenPBIDatabaseDao.delete()
    suspend fun insert(reporteTokenPBIDB: ReporteTokenPBIDB) = reporteTokenPBIDatabaseDao.insert(reporteTokenPBIDB)
    suspend fun upsert(reporteTokenPBIDB: ReporteTokenPBIDB) = reporteTokenPBIDatabaseDao.upsert(reporteTokenPBIDB)
    fun getInformacion(): Flow<List<ReporteTokenPBIDB>> = reporteTokenPBIDatabaseDao.getInformacion().flowOn(Dispatchers.IO).conflate()

}