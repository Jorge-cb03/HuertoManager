package com.example.proyecto.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "diario",
    foreignKeys = [
        ForeignKey(
            entity = JardineraEntity::class,
            parentColumns = ["id"],
            childColumns = ["jardineraId"],
            onDelete = ForeignKey.CASCADE
        ),
        // Relación opcional con Bancal (Set Null para no borrar historial si cambia el bancal)
        ForeignKey(
            entity = BancalEntity::class,
            parentColumns = ["id"],
            childColumns = ["bancalId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("jardineraId"), Index("bancalId")]
)
data class DiarioEntity(
    @PrimaryKey val id: String,
    val jardineraId: String,
    val bancalId: String?, // NULL = Acción Global
    val fecha: Long,
    val tipo: String,      // "RIEGO", "PODA", etc.
    val titulo: String,
    val descripcion: String,
    val fotoUrl: String? = null
)