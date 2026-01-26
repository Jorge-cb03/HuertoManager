package com.example.proyecto.domain.model

// --- ENUMS ---
enum class TipoCultivo {
    HOJA, FRUTO, RAIZ, LEGUMBRE, AROMATICA, OTRO
}

enum class EstadoBancal {
    VACIO,      // Tierra libre
    OCUPADO,    // Planta creciendo
    MUERTO,     // Planta seca
    OCULTO      // Obstáculo
}

enum class TipoEvento {
    RIEGO, FERTILIZANTE, TRATAMIENTO, PODA, SIEMBRA, COSECHA, ELIMINAR, NOTA
}

// --- DATA CLASSES ---

data class Planta(
    val nombre: String,
    val variedad: String,
    val tipo: TipoCultivo,
    val imagenRes: String // String para compatibilidad con BD
)

data class Jardinera(
    val id: String,
    val nombre: String,
    val filas: Int,
    val columnas: Int,
    val bancales: List<Bancal> = emptyList()
)

data class Bancal(
    val id: String,
    val jardineraId: String,
    val indice: Int,
    val estado: EstadoBancal,
    val fechaSiembra: Long? = null,
    val fechaUltimoRiego: Long? = null,
    val fechaUltimoAbono: Long? = null,
    val planta: Planta? = null
)

data class EntradaDiario(
    val id: String,
    val jardineraId: String,
    val bancalId: String?,
    val fecha: Long,
    val tipo: TipoEvento,
    val titulo: String,
    val descripcion: String,
    val fotoUrl: String?
)

data class Producto(
    val id: String,
    val nombre: String,
    val tipo: String,
    val cantidad: String,
    val descripcion: String,
    val icon: String
)