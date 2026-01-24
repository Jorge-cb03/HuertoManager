package com.example.proyecto.ui.garden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.repository.HuertaRepository
import com.example.proyecto.domain.model.EntradaDiario
import com.example.proyecto.domain.model.Jardinera
import com.example.proyecto.domain.model.Producto
import com.example.proyecto.domain.model.ProductType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GardenDetailViewModel(
    private val repository: HuertaRepository,
    private val jardineraId: String
) : ViewModel() {

    // 1. OBTENEMOS LOS BANCALES REALES
    private val _bancalesReales = repository.getJardineraConBancales(jardineraId)

    // 2. JARDINERA ACTUAL (Combinamos Info General + Bancales Reales)
    val jardinera: StateFlow<Jardinera?> = repository.jardineras
        .combine(_bancalesReales) { listaJardineras, listaBancales ->
            val jardineraBase = listaJardineras.find { it.id == jardineraId }
            jardineraBase?.copy(bancales = listaBancales)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // 3. DIARIO
    val diario: StateFlow<List<EntradaDiario>> = repository.getDiarioPorJardinera(jardineraId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 4. SEMILLAS DISPONIBLES
    val semillasDisponibles: StateFlow<List<Producto>> = repository.productos
        .map { todos ->
            todos.filter { prod ->
                try {
                    prod.tipo == ProductType.SEMILLA.name || prod.tipo.equals("SEED", ignoreCase = true)
                } catch (e: Exception) { false }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 5. ACCIONES
    fun sembrar(bancalId: String, semilla: Producto) {
        if (bancalId == "dummy" || bancalId.isBlank()) return

        viewModelScope.launch {
            repository.sembrarBancal(bancalId, semilla.nombre, "Variedad Estándar")
        }
    }

    fun limpiar(bancalId: String) {
        if (bancalId == "dummy" || bancalId.isBlank()) return

        viewModelScope.launch {
            repository.limpiarBancal(bancalId)
        }
    }
}