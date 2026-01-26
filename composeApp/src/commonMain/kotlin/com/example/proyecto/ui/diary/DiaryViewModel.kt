package com.example.proyecto.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.repository.HuertaRepository
import com.example.proyecto.domain.model.EntradaDiario
import com.example.proyecto.domain.model.Jardinera
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
        repository.startSync()
    }

    val tasks: StateFlow<List<DiaryTask>> = combine(
        repository.getHistorial(),
        repository.jardineras
    ) { entradas: List<EntradaDiario>, jardineras: List<Jardinera> ->
        entradas.map { entrada ->
            // Buscamos el nombre de la jardinera o ponemos "General" si es automático/manual
            val nombreJardinera = if (entrada.jardineraId == "manual" || entrada.jardineraId == "auto") {
                "General"
            } else {
                jardineras.find { it.id == entrada.jardineraId }?.nombre ?: "Desconocida"
            }

            val instant = Instant.fromEpochMilliseconds(entrada.fecha)
            val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

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