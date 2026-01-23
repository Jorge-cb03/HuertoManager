package com.example.proyecto.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.repository.HuertaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DiaryViewModel(
    private val repository: HuertaRepository
) : ViewModel() {

    init {
        repository.startSync() // Aseguramos que la sync esté activa
    }

    // COMBINAMOS 2 FUENTES DE DATOS:
    // 1. Todas las entradas del diario (Firebase/Room)
    // 2. Todas las jardineras (para sacar el nombre real, ej: "Tomates" en vez de "ID_123")
    val tasks: StateFlow<List<DiaryTask>> = combine(
        repository.diarioGlobal,
        repository.jardineras
    ) { entradas, jardineras ->
        entradas.map { entrada ->
            // Buscamos el nombre de la jardinera correspondiente
            val nombreJardinera = jardineras.find { it.id == entrada.jardineraId }?.nombre ?: "Jardinera borrada"

            // Convertimos de milisegundos a Fecha y Hora legible
            val instant = Instant.fromEpochMilliseconds(entrada.fecha)
            val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

            // Mapeamos a la clase visual que usa la pantalla
            DiaryTask(
                id = entrada.id,
                title = entrada.titulo,
                description = entrada.descripcion,
                jardineraName = nombreJardinera,
                date = localDateTime.date,
                time = "${localDateTime.hour}:${localDateTime.minute.toString().padStart(2, '0')}"
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun borrarTarea(id: String) {
        viewModelScope.launch {
            repository.borrarEntrada(id)
        }
    }
}