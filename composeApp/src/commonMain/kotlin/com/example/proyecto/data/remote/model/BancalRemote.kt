package com.example.proyecto.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class BancalRemote(
    val id: String,
    val jardineraId: String,
    val indice: Int,
    val estado: String,         // "VACIO", "OCUPADO"
    val fechaSiembra: Long? = null,
    val fechaUltimoRiego: Long? = null,
    val fechaUltimoAbono: Long? = null,

    // Datos de la planta incrustados (Snapshot) para lectura rápida
    val plantaNombre: String? = null,
    val plantaVariedad: String? = null,
    val plantaTipo: String? = null,
    val plantaIcono: String? = null
)