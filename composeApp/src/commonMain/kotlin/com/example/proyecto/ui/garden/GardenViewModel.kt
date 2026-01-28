package com.example.proyecto.ui.garden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.database.entity.BancalEntity
import com.example.proyecto.data.repository.JardineraRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class GardenViewModel(private val repository: JardineraRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.inicializarDatosPrueba()
        }
    }

    // Flujo de todas las jardineras para la paginación
    val jardineras = repository.getJardineras()

    // Obtener bancales de una jardinera específica
    fun getBancales(jardineraId: Long): Flow<List<BancalEntity>> {
        return repository.getBancales(jardineraId)
    }

    // Crear jardinera nueva con la matriz 2x4 por defecto
    fun crearNuevaJardinera(nombre: String) {
        viewModelScope.launch {
            repository.crearJardineraConBancales(nombre, 4, 2)
        }
    }

    // Lógica de plantado con stock y API
    fun plantar(bancalId: Long, slug: String) {
        viewModelScope.launch {
            repository.plantarEnBancal(bancalId, slug)
        }
    }
}