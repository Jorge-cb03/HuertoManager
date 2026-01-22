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

    // Esta lista se actualizará sola cuando cambie la base de datos
    val jardineras: StateFlow<List<Jardinera>> = repository.jardineras
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun crearJardineraTest() {
        viewModelScope.launch {
            repository.crearJardinera("Jardinera ${(1..99).random()}")
        }
    }
}