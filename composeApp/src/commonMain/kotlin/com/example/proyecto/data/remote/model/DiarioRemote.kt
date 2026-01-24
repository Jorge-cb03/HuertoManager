package com.example.proyecto.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class DiarioRemote(
    val id: String,
    val jardineraId: String,
    val bancalId: String? = null, // NULL = Global
    val fecha: Long,
    val tipo: String,             // "RIEGO", "PODA"...
    val titulo: String,
    val descripcion: String,
    val fotoUrl: String? = null
)