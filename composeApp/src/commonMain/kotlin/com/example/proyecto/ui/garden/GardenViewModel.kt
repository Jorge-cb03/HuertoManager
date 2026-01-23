package com.example.proyecto.ui.garden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.repository.HuertaRepository
import com.example.proyecto.domain.model.Jardinera
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GardenViewModel(
    private val repository: HuertaRepository
) : ViewModel() {

    // 1. ESTADO: La UI observa esto.
    val jardineras: StateFlow<List<Jardinera>> = repository.jardineras
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // ACTIVAMOS LA SINCRONIZACIÓN AL INICIAR
        repository.startSync()
    }

    // He renombrado esta función para que coincida con tu GardenScreen
    fun crearJardineraTest() {
        viewModelScope.launch {
            // Crea una con un nombre aleatorio para probar
            repository.crearJardinera("Jardinera ${(1..99).random()}")
        }
    }
}