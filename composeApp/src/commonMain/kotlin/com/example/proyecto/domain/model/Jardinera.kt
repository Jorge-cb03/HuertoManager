package com.example.proyecto.domain.model

// --- CLASE PLANTA ---
data class Planta(
    val nombre: String,
    val variedad: String = "",
    val tipo: TipoCultivo = TipoCultivo.OTRO,
    val diasCosechaEstimados: Int = 90,
    val imagenRes: String = "plant_default"
)

// --- CLASE BANCAL ---
data class Bancal(
    val id: String,
    val jardineraId: String,
    val indice: Int,
    val planta: Planta? = null,
    val estado: EstadoBancal = EstadoBancal.VACIO,
    val fechaSiembra: Long? = null,
    val fechaUltimoRiego: Long? = null,
    val fechaUltimoAbono: Long? = null
) {
    // Esta función necesita que EntradaDiario y TipoEvento existan y compilen bien
    fun aplicarEvento(evento: EntradaDiario): Bancal {
        if (evento.bancalId != null && evento.bancalId != this.id) return this

        return when (evento.tipo) {
            TipoEvento.RIEGO -> this.copy(fechaUltimoRiego = evento.fecha)
            TipoEvento.FERTILIZANTE -> this.copy(fechaUltimoAbono = evento.fecha)
            TipoEvento.COSECHA, TipoEvento.ELIMINAR -> this.copy(
                planta = null,
                estado = EstadoBancal.VACIO,
                fechaSiembra = null,
                fechaUltimoRiego = null,
                fechaUltimoAbono = null
            )
            else -> this
        }
    }
}

// --- CLASE JARDINERA ---
data class Jardinera(
    val id: String,
    val nombre: String,
    val filas: Int = 2,
    val columnas: Int = 4,
    val bancales: List<Bancal> = emptyList()
) {
    fun getCoordenada(indice: Int): String {
        val fila = (indice / columnas) + 1
        val col = (indice % columnas) + 1
        return "$fila:$col"
    }

    fun getIndicesVecinos(indice: Int): List<Int> {
        val f = indice / columnas
        val c = indice % columnas
        val vecinos = mutableListOf<Int>()
        if (f > 0) vecinos.add(indice - columnas)
        if (f < filas - 1) vecinos.add(indice + columnas)
        if (c > 0) vecinos.add(indice - 1)
        if (c < columnas - 1) vecinos.add(indice + 1)
        return vecinos
    }
}

// --- NUEVO: MODELO DE PRODUCTO (INVENTARIO) ---
data class Producto(
    val id: String,
    val nombre: String,
    val tipo: String, // Ej: "SEMILLA", "HERRAMIENTA"
    val cantidad: String,
    val descripcion: String,
    val icon: String = "default_product"
)

// Enum simple para la UI (opcional, úsalo si lo necesitas en la vista)
enum class ProductType {
    SEMILLA, FERTILIZANTE, HERRAMIENTA, OTRO
}