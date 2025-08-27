package ita.tech.eveniment.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CalendarioAlarmaDB")
data class CalendarioAlarmaDB(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    @ColumnInfo(name = "alarmaId")
    val alarmaId: Int
)
