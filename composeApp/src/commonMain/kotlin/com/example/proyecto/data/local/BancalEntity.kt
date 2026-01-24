package com.example.proyecto.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "bancales",
    foreignKeys = [
        ForeignKey(
            entity = JardineraEntity::class,
            parentColumns = ["id"],
            childColumns = ["jardineraId"],
            onDelete = ForeignKey.CASCADE // Si borras jardinera, se borran sus huecos
        )
    ],
    indices = [Index("jardineraId")]
)
data class BancalEntity(
    @PrimaryKey val id: String,
    val jardineraId: String,
    val indice: Int,         // Posición lineal (0..7)

    // Estado del Hueco
    val estado: String,      // "VACIO", "OCUPADO", "MUERTO"
    val fechaSiembra: Long?,
    val fechaUltimoRiego: Long?,
    val fechaUltimoAbono: Long?,

    // Datos de la Planta (snapshot)
    val plantaNombre: String? = null,
    val plantaVariedad: String? = null,
    val plantaTipo: String? = null,
    val plantaIcono: String? = null
)