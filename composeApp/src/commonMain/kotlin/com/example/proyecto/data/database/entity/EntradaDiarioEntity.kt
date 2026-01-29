package com.example.proyecto.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entradas_diario") // <--- ESTO ES CRÍTICO
data class EntradaDiarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bancalId: Long,
    val tipoAccion: String, // CREACION, SIEMBRA, RIEGO, etc.
    val descripcion: String,
    val fecha: Long = 0L
)