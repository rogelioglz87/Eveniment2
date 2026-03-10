package ita.tech.eveniment.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import ita.tech.eveniment.model.UsuarioTokenPBIDB
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioTokenPBIDatabaseDao {
    @Query("SELECT * FROM UsuarioTokenPBIDB")
    fun getInformacion(): Flow<List<UsuarioTokenPBIDB>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usuarioTokenPBIDB: UsuarioTokenPBIDB)

    @Query("DELETE FROM UsuarioTokenPBIDB")
    suspend fun delete()

    @Upsert
    suspend fun upsert(usuarioTokenPBIDB: UsuarioTokenPBIDB)

}