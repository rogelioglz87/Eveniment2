package ita.tech.eveniment.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ita.tech.eveniment.model.CalendarioAlarmaDB
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarioAlarmaDatabaseDao {
    @Query("SELECT * FROM CalendarioAlarmaDB")
    fun getInformacion(): Flow<List<CalendarioAlarmaDB>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(calendarioAlarmaDB: CalendarioAlarmaDB)

    @Query("DELETE FROM CalendarioAlarmaDB")
    suspend fun delete()
}