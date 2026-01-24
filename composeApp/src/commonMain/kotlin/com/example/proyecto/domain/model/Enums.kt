package com.example.proyecto.domain.model

enum class TipoCultivo {
    HOJA, FRUTO, RAIZ, LEGUMBRE, AROMATICA, OTRO
}

// El estado visual del hueco en la matriz
enum class EstadoBancal {
    VACIO,          // Hueco disponible (Tierra)
    OCUPADO,        // Hay una planta viva
    MUERTO          // Planta seca/enferma (necesita limpieza)
}

// Acciones que el usuario puede realizar
enum class TipoEvento {
    RIEGO,          // Sube humedad
    FERTILIZANTE,   // Sube nutrientes
    TRATAMIENTO,    // Cura plagas/enfermedades
    PODA,           // Mantenimiento
    SIEMBRA,        // Inicio de vida
    COSECHA,        // Fin de vida exitoso
    ELIMINAR,       // Fin de vida por error
    NOTA            // Texto libre
}