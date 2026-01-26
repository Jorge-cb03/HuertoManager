package com.example.proyecto.ui.garden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.repository.HuertaRepository
import com.example.proyecto.domain.model.Bancal
import com.example.proyecto.domain.model.Producto
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GardenPageUi(
    val id: String,
    val name: String,
    val columns: Int,
    val slots: List<Bancal>
)

class GardenViewModel(
    private val repository: HuertaRepository
) : ViewModel() {

    val gardenPages: StateFlow<List<GardenPageUi>> = repository.jardineras
        .combine(repository.bancalesGlobales) { jardineras, todosLosBancales ->
            jardineras.map { jardinera ->
                val susBancales = todosLosBancales
                    .filter { it.jardineraId == jardinera.id }
                    .sortedBy { it.indice }

                GardenPageUi(
                    id = jardinera.id,
                    name = jardinera.nombre,
                    columns = jardinera.columnas,
                    slots = susBancales
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val semillasDisponibles: StateFlow<List<Producto>> = repository.productos
        .map { productos ->
            productos.filter {
                (it.tipo.equals("SEMILLA", ignoreCase = true) || it.tipo.equals("SEED", ignoreCase = true)) &&
                        (it.cantidad.toIntOrNull() ?: 0) > 0
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun crearJardinera(nombre: String, filas: Int = 4, columnas: Int = 2) {
        if (nombre.isBlank()) return
        viewModelScope.launch { repository.crearJardinera(nombre.trim(), filas, columnas) }
    }

    fun redimensionarJardinera(id: String, filasActuales: Int, columnas: Int, nuevasFilas: Int) {
        viewModelScope.launch {
            repository.redimensionarJardinera(id, filasActuales, columnas, nuevasFilas)
        }
    }

    fun borrarJardinera(id: String) {
        viewModelScope.launch { repository.borrarJardinera(id) }
    }

    fun renombrarJardinera(id: String, nombre: String) {
        viewModelScope.launch { repository.renombrarJardinera(id, nombre) }
    }

    fun sembrar(bancalId: String, producto: Producto) {
        viewModelScope.launch { repository.sembrarBancal(bancalId, producto) }
    }

    fun cosechar(bancalId: String) {
        viewModelScope.launch { repository.limpiarBancal(bancalId) }
    }
}