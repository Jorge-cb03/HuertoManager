package com.example.proyecto.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class DiarioRemote(
    val id: String = "",
    val jardineraId: String = "", // Para saber a qué planta pertenece
    val fecha: Long = 0L,
    val titulo: String = "",
    val descripcion: String = "",
    val tipo: String = "Riego", // "Riego", "Poda", "Abono", "Otros"
    val fotoUrl: String? = null
)