package com.example.proyecto.domain.model

enum class TipoCultivo {
    HOJA, FRUTO, RAIZ, LEGUMBRE, AROMATICA, OTRO
}

enum class EstadoBancal {
    VACIO,      // Tierra libre
    OCUPADO,    // Planta creciendo
    MUERTO      // Planta seca
}

enum class TipoEvento {
    RIEGO, FERTILIZANTE, TRATAMIENTO, PODA, SIEMBRA, COSECHA, ELIMINAR, NOTA
}