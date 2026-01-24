package com.example.proyecto.ui.garden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.repository.HuertaRepository
import com.example.proyecto.domain.model.EntradaDiario // Usamos el modelo de Dominio
import com.example.proyecto.domain.model.Jardinera     // <--- EL IMPORT QUE FALTABA
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class GardenDetailViewModel(
    private val repository: HuertaRepository,
    private val jardineraId: String
) : ViewModel() {

    // 1. Buscamos la jardinera actual en la lista global del repositorio
    val jardinera: StateFlow<Jardinera?> = repository.jardineras
        .combine(flowOf(jardineraId)) { lista, id ->
            lista.find { it.id == id }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // 2. Obtenemos el diario filtrado (Usamos el método nuevo del Repo)
    val diario: StateFlow<List<EntradaDiario>> = repository.getDiarioPorJardinera(jardineraId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}