package com.example.proyecto.ui.garden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.local.DiarioEntity
import com.example.proyecto.data.repository.HuertaRepository
import com.example.proyecto.domain.model.Jardinera
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class GardenDetailViewModel(
    private val repository: HuertaRepository,
    private val jardineraId: String
) : ViewModel() {

    // Combinamos la lista global para encontrar LA jardinera actual
    val jardinera: StateFlow<Jardinera?> = repository.jardineras
        .combine(kotlinx.coroutines.flow.flowOf(jardineraId)) { lista, id ->
            lista.find { it.id == id }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Obtenemos el diario específico de esta planta
    val diario: StateFlow<List<DiarioEntity>> = repository.getDiario(jardineraId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}