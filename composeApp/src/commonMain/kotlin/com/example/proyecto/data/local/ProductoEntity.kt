package com.example.proyecto.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val tipo: String, // "TOOL", "SEED", "CHEMICAL", "OTHER"
    val cantidad: String,
    val descripcion: String
)