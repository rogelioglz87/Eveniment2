package ita.tech.eveniment.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import ita.tech.eveniment.model.ReporteTokenPBIDB
import kotlinx.coroutines.flow.Flow

@Dao
interface ReporteTokenPBIDatabaseDao {

    @Query("SELECT * FROM ReporteTokenPBIDB")
    fun getInformacion(): Flow<List<ReporteTokenPBIDB>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reporteTokenPBIDB: ReporteTokenPBIDB)

    @Query("DELETE FROM ReporteTokenPBIDB")
    suspend fun delete()

    @Upsert
    suspend fun upsert(reporteTokenPBIDB: ReporteTokenPBIDB)

}