package com.example.proyecto.domain.model

data class Jardinera(
    val id: String,
    val nombre: String,     // Ej: "Jardinera A1"
    val cultivo: String?,   // Ej: "Tomates", puede ser null si está vacía
    val estado: EstadoJardinera, // Ahora hace referencia al que está en Enums.kt
    val icon: String = "🌱" // Usaremos emojis por ahora para ver qué es cada cosa rápido
)