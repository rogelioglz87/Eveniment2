package ita.tech.eveniment.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "InformacionPantallaDB")
data class InformacionPantallaDB(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0
)
