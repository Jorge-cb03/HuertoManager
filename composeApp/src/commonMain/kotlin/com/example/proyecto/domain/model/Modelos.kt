package com.example.proyecto.domain.model

// Datos botánicos (Lo que es la planta en sí)
data class Planta(
    val nombre: String,
    val variedad: String = "",
    val tipo: TipoCultivo = TipoCultivo.OTRO,
    val diasCosechaEstimados: Int = 90,
    val imagenRes: String = "plant_default"
)

// Datos físicos (El hueco en la matriz)
data class Bancal(
    val id: String,
    val jardineraId: String,
    val indice: Int,            // Posición en Grid (0..7)
    val planta: Planta? = null,
    val estado: EstadoBancal = EstadoBancal.VACIO,
    // --- ESTADÍSTICAS INDEPENDIENTES ---
    val fechaSiembra: Long? = null,
    val fechaUltimoRiego: Long? = null,
    val fechaUltimoAbono: Long? = null
) {
    // AUTOMATIZACIÓN: El bancal reacciona solo si el evento es para él
    fun aplicarEvento(evento: EntradaDiario): Bancal {
        // Si el evento tiene bancalId, debe coincidir con este.
        // Si es null (evento global), afecta a todos.
        if (evento.bancalId != null && evento.bancalId != this.id) return this

        return when (evento.tipo) {
            TipoEvento.RIEGO -> this.copy(fechaUltimoRiego = evento.fecha)
            TipoEvento.FERTILIZANTE -> this.copy(fechaUltimoAbono = evento.fecha)

            // Si cosechamos o eliminamos, limpiamos el hueco
            TipoEvento.COSECHA, TipoEvento.ELIMINAR -> this.copy(
                planta = null,
                estado = EstadoBancal.VACIO,
                fechaSiembra = null,
                fechaUltimoRiego = null,
                fechaUltimoAbono = null
            )
            // La siembra se gestiona aparte (al arrastrar)
            else -> this
        }
    }
}

// El contenedor
data class Jardinera(
    val id: String,
    val nombre: String,
    val filas: Int = 2,
    val columnas: Int = 4,
    val bancales: List<Bancal> = emptyList()
) {
    // Lógica de coordenadas para dibujar la UI ("1:1", "1:2"...)
    fun getCoordenada(indice: Int): String {
        val fila = (indice / columnas) + 1
        val col = (indice % columnas) + 1
        return "$fila:$col"
    }

    // Lógica de Vecinos para calcular alertas de enemigos
    fun getIndicesVecinos(indice: Int): List<Int> {
        val f = indice / columnas
        val c = indice % columnas
        val vecinos = mutableListOf<Int>()

        if (f > 0) vecinos.add(indice - columnas)        // Arriba
        if (f < filas - 1) vecinos.add(indice + columnas) // Abajo
        if (c > 0) vecinos.add(indice - 1)               // Izq
        if (c < columnas - 1) vecinos.add(indice + 1)    // Der

        return vecinos
    }
}