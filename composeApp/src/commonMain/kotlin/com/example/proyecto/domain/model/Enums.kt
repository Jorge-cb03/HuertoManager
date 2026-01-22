package com.example.proyecto.domain.model

// Enum para el tipo de acción en el diario
enum class TipoEntrada {
    RIEGO,
    FERTILIZANTE,
    PODA,
    TRASPLANTE,
    COSECHA,
    PLAGA,
    OTRO
}

// Enum para el estado de la jardinera (Lo usamos en HuertaRepository)
enum class EstadoJardinera {
    VACIO,
    OCUPADO,
    ENFERMO
}