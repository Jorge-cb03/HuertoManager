package com.example.proyecto.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val tipo: String,
    val cantidad: String,
    val descripcion: String,
    val icon: String // <--- NUEVO CAMPO
)