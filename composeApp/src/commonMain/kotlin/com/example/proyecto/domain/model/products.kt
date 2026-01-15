package com.example.proyecto.domain.model

data class Producto(
    val id: String,
    val nombre: String,
    val descripcion: String, // Información detallada
    val stock: Int,          // Cantidad disponible
    val icon: String         // Emoji o icono
)