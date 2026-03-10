package ita.tech.eveniment.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "UsuarioTokenPBIDB")
data class UsuarioTokenPBIDB(
    @PrimaryKey
    val idUsuario: Long,
    @ColumnInfo(name = "token")
    val token: String
)
