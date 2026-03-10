package ita.tech.eveniment.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "ReporteTokenPBIDB")
data class ReporteTokenPBIDB(
    @PrimaryKey
    val idReporte: Long,
    @ColumnInfo(name = "token")
    val token: String
)
