package com.example.proyecto.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class JardineraRemote(
    val id: String = "",
    val nombre: String = "",
    val cultivo: String? = null,
    val estado: String = "VACIO",
    val icon: String = "🌱"
)