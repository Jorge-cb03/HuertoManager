package com.example.proyecto.ui.garden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.database.entity.*
import com.example.proyecto.data.repository.JardineraRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GardenViewModel(private val repository: JardineraRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.inicializarDatosPrueba() // Carga los datos iniciales (Tomate Cherry, etc.)
        }
    }

    // Flujo de jardineras desde la base de datos
    val jardineras: StateFlow<List<JardineraEntity>> = repository.getJardineras()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getBancales(jardineraId: Long) = repository.getBancales(jardineraId)

    fun getHistorial(bancalId: Long) = repository.getHistorialBancal(bancalId)

    suspend fun getBancalById(id: Long) = repository.getBancalById(id)

    fun crearNuevaJardinera(nombre: String) {
        viewModelScope.launch {
            repository.crearJardineraConBancales(nombre, 4, 2)
        }
    }

    fun plantar(bancalId: Long, slug: String) {
        viewModelScope.launch {
            repository.plantarEnBancal(bancalId, slug)
        }
    }

    val historialGeneral: StateFlow<List<EntradaDiarioEntity>> = repository.getTodoElHistorial()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // También necesitamos esto para ProductsScreen
    fun getProductos(): Flow<List<ProductoEntity>> = repository.getProductos()

    suspend fun getProductoById(id: Long) = repository.getProductoById(id)

    fun guardarEntradaDiario(bancalId: Long, tipo: String, desc: String, fecha: Long) {
        viewModelScope.launch {
            repository.insertarEntradaDiario(EntradaDiarioEntity(
                bancalId = bancalId,
                tipoAccion = tipo,
                descripcion = desc,
                fecha = fecha
            ))
        }
    }

    // En GardenViewModel.kt
    fun guardarAccionMultiple(bancalIds: List<Long>, tipo: String, desc: String, fecha: Long) {
        viewModelScope.launch {
            bancalIds.forEach { id ->
                repository.insertarEntradaDiario(
                    EntradaDiarioEntity(
                        bancalId = id,
                        tipoAccion = tipo,
                        descripcion = desc,
                        fecha = fecha
                    )
                )
            }
        }
    }
    fun eliminarEntradaDiario(id: Long) {
        viewModelScope.launch {
            repository.eliminarEntradaDiario(id)
        }
    }

    suspend fun getEntradaDiarioById(id: Long): EntradaDiarioEntity? {
        return repository.getEntradaDiarioById(id)
    }

    // Actualizado para permitir edición (pasando el ID)
    fun guardarEntradaDiario(bancalId: Long, tipo: String, desc: String, fecha: Long, id: Long = 0) {
        viewModelScope.launch {
            repository.insertarEntradaDiario(EntradaDiarioEntity(
                id = id, // Si es > 0, Room sobreescribe el existente
                bancalId = bancalId,
                tipoAccion = tipo,
                descripcion = desc,
                fecha = fecha
            ))
        }
    }
}