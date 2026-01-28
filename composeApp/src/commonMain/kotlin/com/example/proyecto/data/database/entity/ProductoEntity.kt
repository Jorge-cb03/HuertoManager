package com.example.proyecto.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val categoria: String, // SEMILLA, HERRAMIENTA, QUIMICO
    val stock: Int,
    val openFarmSlug: String? = null // Para vincular semillas con la API
)