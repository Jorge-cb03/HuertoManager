package com.example.proyecto.ui.garden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.repository.HuertaRepository
import com.example.proyecto.domain.model.Bancal
import com.example.proyecto.domain.model.EntradaDiario
import com.example.proyecto.domain.model.Jardinera
import com.example.proyecto.domain.model.Producto
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GardenDetailViewModel(
    private val repository: HuertaRepository,
    private val jardineraId: String
) : ViewModel() {

    // 1. OBTENEMOS LOS BANCALES REALES (Filtrando la lista global)
    private val _bancalesReales = repository.bancalesGlobales.map { allBancales ->
        allBancales
            .filter { it.jardineraId == jardineraId }
            .sortedBy { it.indice }
    }

    // 2. JARDINERA ACTUAL
    val jardinera: StateFlow<Jardinera?> = repository.jardineras
        .combine(_bancalesReales) { listaJardineras: List<Jardinera>, listaBancales: List<Bancal> ->

            val jardineraBase = listaJardineras.find { it.id == jardineraId }

            if (jardineraBase != null) {
                // CORRECCIÓN AQUÍ: Usamos 'bancales' en lugar de 'slots'
                // Esto depende de cómo definiste 'data class Jardinera' en el paquete domain.model
                jardineraBase.copy(bancales = listaBancales)
            } else {
                null
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // 3. DIARIO
    val diario: StateFlow<List<EntradaDiario>> = repository.getHistorial()
        .map { entradas ->
            entradas.filter { it.jardineraId == jardineraId }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 4. SEMILLAS DISPONIBLES
    val semillasDisponibles: StateFlow<List<Producto>> = repository.productos
        .map { todos ->
            todos.filter { prod ->
                prod.tipo.equals("SEMILLA", ignoreCase = true) ||
                        prod.tipo.equals("SEED", ignoreCase = true)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 5. ACCIONES
    fun sembrar(bancalId: String, semilla: Producto) {
        if (bancalId == "dummy" || bancalId.isBlank()) return

        viewModelScope.launch {
            repository.sembrarBancal(bancalId, semilla)
        }
    }

    fun limpiar(bancalId: String) {
        if (bancalId == "dummy" || bancalId.isBlank()) return

        viewModelScope.launch {
            repository.limpiarBancal(bancalId)
        }
    }
}