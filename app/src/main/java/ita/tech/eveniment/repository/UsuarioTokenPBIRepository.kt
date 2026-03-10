package ita.tech.eveniment.repository

import ita.tech.eveniment.model.UsuarioTokenPBIDB
import ita.tech.eveniment.room.UsuarioTokenPBIDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UsuarioTokenPBIRepository @Inject constructor(private val usuarioTokenPBIDatabaseDao: UsuarioTokenPBIDatabaseDao) {
    suspend fun delete() = usuarioTokenPBIDatabaseDao.delete()
    suspend fun insert(usuarioTokenPBIDB: UsuarioTokenPBIDB) = usuarioTokenPBIDatabaseDao.insert(usuarioTokenPBIDB)
    suspend fun upsert(usuarioTokenPBIDB: UsuarioTokenPBIDB) = usuarioTokenPBIDatabaseDao.upsert(usuarioTokenPBIDB)
    fun getInformacion(): Flow<List<UsuarioTokenPBIDB>> = usuarioTokenPBIDatabaseDao.getInformacion().flowOn(Dispatchers.IO).conflate()
}