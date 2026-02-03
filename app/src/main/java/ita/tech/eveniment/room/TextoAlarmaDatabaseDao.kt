package ita.tech.eveniment.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ita.tech.eveniment.model.CalendarioAlarmaDB
import ita.tech.eveniment.model.TextoAlarmaDB
import kotlinx.coroutines.flow.Flow

@Dao
interface TextoAlarmaDatabaseDao {

    @Query("SELECT * FROM TextoAlarmaDB")
    fun getInformacion(): Flow<List<TextoAlarmaDB>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(textoAlarmaDB: TextoAlarmaDB)

    @Query("DELETE FROM TextoAlarmaDB")
    suspend fun delete()

}