package com.example.proyecto.domain.model

import kotlinx.datetime.LocalDate

data class EntradaDiario(
    val id: String,
    val fecha: LocalDate,      // Fecha real (no String ni Long)
    val titulo: String,
    val descripcion: String,
    val tipo: TipoEntrada,     // <--- IMPORTANTE: Usamos el Enum, no String
    val fotoUrl: String? = null // Opcional, para cuando metamos fotos
)