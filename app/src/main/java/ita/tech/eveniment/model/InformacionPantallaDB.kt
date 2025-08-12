package ita.tech.eveniment.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "InformacionPantallaDB")
data class InformacionPantallaDB(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    @ColumnInfo(name = "concepto")
    val concepto: String,
    @ColumnInfo(name = "valor")
    val valor: String
)
