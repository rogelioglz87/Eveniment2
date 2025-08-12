package ita.tech.eveniment.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ita.tech.eveniment.model.InformacionPantallaDB
import kotlinx.coroutines.flow.Flow

@Dao
interface InformacionPantallaDatabaseDao {

    @Query("DELETE FROM InformacionPantallaDB WHERE concepto = :concepto")
    suspend fun clear(concepto: String)

    @Query("SELECT * FROM InformacionPantallaDB WHERE concepto = :concepto")
    fun getInformacion(concepto: String): Flow<InformacionPantallaDB>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(informacionPantallaDB: InformacionPantallaDB)

}