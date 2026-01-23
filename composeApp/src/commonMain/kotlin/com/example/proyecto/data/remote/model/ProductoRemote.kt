package com.example.proyecto.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductoRemote(
    val id: String = "",
    val nombre: String = "",
    val tipo: String = "OTHER",
    val cantidad: String = "",
    val descripcion: String = ""
)