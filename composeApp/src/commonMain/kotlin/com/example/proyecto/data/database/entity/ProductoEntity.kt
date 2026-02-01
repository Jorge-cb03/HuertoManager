package com.example.proyecto.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val categoria: String,
    val stock: Double,
    val perenualId: Int? = null, // VUELVE A SER INT
    val imagenUrl: String? = null,
    val nombreCientifico: String? = null,
    val notasCultivo: String? = null
)