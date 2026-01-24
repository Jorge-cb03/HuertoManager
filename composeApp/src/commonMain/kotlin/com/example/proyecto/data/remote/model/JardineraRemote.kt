package com.example.proyecto.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class JardineraRemote(
    val id: String,
    val nombre: String,
    val filas: Int = 2,      // Default 2
    val columnas: Int = 4,   // Default 4
    val icon: String = "default_garden",
    val cultivo: String = "", // Mantener compatibilidad si existía
    val estado: String = ""   // Mantener compatibilidad si existía
)