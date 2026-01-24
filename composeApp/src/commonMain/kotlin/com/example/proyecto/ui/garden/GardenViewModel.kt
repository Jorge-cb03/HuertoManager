package com.example.proyecto.ui.garden

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.repository.HuertaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// --- MOVEMOS LA CLASE AQUÍ PARA EVITAR ERRORES DE COMPILACIÓN CRUZADOS ---
data class JardineraUi(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val icon: ImageVector = Icons.Default.Eco,
    val color: Color = Color(0xFF4CAF50)
)

class GardenViewModel(
    private val repository: HuertaRepository
) : ViewModel() {

    // Transformamos Dominio (Jardinera) -> UI (JardineraUi)
    val jardinerasUi: StateFlow<List<JardineraUi>> = repository.jardineras
        .map { listaDominio ->
            listaDominio.map { jardinera ->
                JardineraUi(
                    id = jardinera.id,
                    nombre = jardinera.nombre,
                    descripcion = "${jardinera.filas}x${jardinera.columnas} • ${jardinera.bancales.size} huecos"
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun crearJardinera(nombre: String, filas: Int, columnas: Int) {
        viewModelScope.launch {
            repository.crearJardinera(nombre, filas, columnas)
        }
    }

    fun borrarJardinera(id: String) {
        viewModelScope.launch {
            repository.borrarJardinera(id)
        }
    }
}