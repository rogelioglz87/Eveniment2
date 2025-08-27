package ita.tech.eveniment.room

import androidx.room.Database
import androidx.room.RoomDatabase
import ita.tech.eveniment.model.CalendarioAlarmaDB
import ita.tech.eveniment.model.InformacionPantallaDB

@Database(entities = [InformacionPantallaDB::class, CalendarioAlarmaDB::class], version = 2, exportSchema = false)
abstract class EvenimentDatabase: RoomDatabase() {

    abstract fun informacionPantallaDao(): InformacionPantallaDatabaseDao

    abstract fun calendarioAlarmaDao(): CalendarioAlarmaDatabaseDao

}