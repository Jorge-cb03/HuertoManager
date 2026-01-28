package com.example.proyecto.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenFarmResponse(
    // Las búsquedas siempre devuelven una lista []
    val data: List<CropData>
)

@Serializable
data class CropData(
    val id: String,
    val attributes: CropAttributes
)

@Serializable
data class CropAttributes(
    val name: String,
    val slug: String,
    val description: String? = null,
    // Coincide con 'main_image_path' del backend
    @SerialName("main_image_path") val mainImagePath: String? = null
)