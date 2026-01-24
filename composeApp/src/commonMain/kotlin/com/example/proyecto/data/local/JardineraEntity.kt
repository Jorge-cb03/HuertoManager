package com.example.proyecto.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jardineras")
data class JardineraEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val filas: Int,     // Nuevo: Altura del grid
    val columnas: Int,  // Nuevo: Anchura del grid
    val icon: String = "default_garden"
)