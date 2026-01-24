package com.example.proyecto.domain.model

data class EntradaDiario(
    val id: String,
    val jardineraId: String,
    val bancalId: String? = null,
    val fecha: Long,
    val tipo: TipoEvento, // Usa el Enum de arriba
    val titulo: String,
    val descripcion: String,
    val fotoUrl: String? = null
)