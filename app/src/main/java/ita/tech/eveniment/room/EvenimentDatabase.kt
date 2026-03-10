package ita.tech.eveniment.room

import androidx.room.Database
import androidx.room.RoomDatabase
import ita.tech.eveniment.model.CalendarioAlarmaDB
import ita.tech.eveniment.model.InformacionPantallaDB
import ita.tech.eveniment.model.ReporteTokenPBIDB
import ita.tech.eveniment.model.TextoAlarmaDB
import ita.tech.eveniment.model.UsuarioTokenPBIDB

@Database(
    entities = [InformacionPantallaDB::class, CalendarioAlarmaDB::class, TextoAlarmaDB::class, UsuarioTokenPBIDB::class, ReporteTokenPBIDB::class],
    version = 9,
    exportSchema = false
)
abstract class EvenimentDatabase : RoomDatabase() {

    abstract fun informacionPantallaDao(): InformacionPantallaDatabaseDao

    abstract fun calendarioAlarmaDao(): CalendarioAlarmaDatabaseDao

    abstract fun textoAlarmaDao(): TextoAlarmaDatabaseDao

    abstract fun usuarioTokenPBIDao(): UsuarioTokenPBIDatabaseDao

    abstract fun reporteTokenPBIDao(): ReporteTokenPBIDatabaseDao
}