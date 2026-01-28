package com.example.proyecto.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "diarios",
    foreignKeys = [
        ForeignKey(
            entity = BancalEntity::class,
            parentColumns = ["id"],
            childColumns = ["bancalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bancalId")]
)
data class EntradaDiarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bancalId: Long,
    val tipoAccion: String, // Riego, Abono, Siembra, Extraordinaria
    val descripcion: String,
    val fecha: Long = System.currentTimeMillis(),
    val imagenLocalUri: String? = null // Ruta de la foto en el dispositivo
)