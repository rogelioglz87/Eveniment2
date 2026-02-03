package ita.tech.eveniment.room

import androidx.room.Database
import androidx.room.RoomDatabase
import ita.tech.eveniment.model.CalendarioAlarmaDB
import ita.tech.eveniment.model.InformacionPantallaDB
import ita.tech.eveniment.model.TextoAlarmaDB

@Database(entities = [InformacionPantallaDB::class, CalendarioAlarmaDB::class, TextoAlarmaDB::class], version = 3, exportSchema = false)
abstract class EvenimentDatabase: RoomDatabase() {

    abstract fun informacionPantallaDao(): InformacionPantallaDatabaseDao

    abstract fun calendarioAlarmaDao(): CalendarioAlarmaDatabaseDao

    abstract fun textoAlarmaDao(): TextoAlarmaDatabaseDao
}