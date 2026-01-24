package com.example.proyecto.domain.model

data class EntradaDiario(
    val id: String,
    val jardineraId: String,
    // LA CLAVE: Si tiene valor, la acción es ESPECÍFICA para ese hueco.
    val bancalId: String? = null,
    val fecha: Long,
    val tipo: TipoEvento,
    val titulo: String,
    val descripcion: String,
    val fotoUrl: String? = null
)