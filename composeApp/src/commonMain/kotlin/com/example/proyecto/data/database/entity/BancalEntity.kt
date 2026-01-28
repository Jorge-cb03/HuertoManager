package com.example.proyecto.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bancales",
    foreignKeys = [
        ForeignKey(
            entity = JardineraEntity::class,
            parentColumns = ["id"],
            childColumns = ["jardineraId"],
            onDelete = ForeignKey.CASCADE // Si borras la jardinera, se borran sus bancales
        )
    ],
    indices = [Index("jardineraId")]
)
data class BancalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val jardineraId: Long,
    val fila: Int,
    val columna: Int,
    val cultivoSlug: String? = null, // ID de OpenFarm
    val nombreCultivo: String? = null,
    val imagenUrl: String? = null,   // URL de la imagen de la API
    val fechaSiembra: Long? = null,  // Timestamp
    val diasParaCosecha: Int? = null // Dato de OpenFarm para calcular la fecha
)