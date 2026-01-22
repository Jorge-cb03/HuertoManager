package com.example.proyecto.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// 1. Tabla para las Jardineras
// Fíjate que usamos tipos simples (String, Int) para evitar líos con Room
@Entity(tableName = "jardineras")
data class JardineraEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val cultivo: String?,   // Puede ser null si está vacía
    val estado: String,     // Guardamos el Enum como String ("VACIO", "OCUPADO")
    val icon: String
)

// 2. Tabla para el Diario
@Entity(tableName = "diario")
data class DiarioEntity(
    @PrimaryKey val id: String,
    val jardineraId: String, // Clave foránea (relación)
    val fecha: Long,         // Guardamos fechas como milisegundos (Long) es lo más fácil
    val titulo: String,
    val descripcion: String,
    val tipo: String,        // "Riego", "Poda", etc.
    val fotoUrl: String? = null
)