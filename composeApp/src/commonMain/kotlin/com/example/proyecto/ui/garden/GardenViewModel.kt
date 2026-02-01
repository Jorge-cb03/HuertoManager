package com.example.proyecto.ui.garden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.database.entity.*
import com.example.proyecto.data.repository.JardineraRepository
import com.example.proyecto.data.repository.PerenualSpecies
import com.example.proyecto.domain.model.ProductType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GardenViewModel(private val repository: JardineraRepository) : ViewModel() {

    val jardineras: StateFlow<List<JardineraEntity>> = repository.getJardineras()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historialGeneral: StateFlow<List<EntradaDiarioEntity>> = repository.getTodoElHistorial()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alerts: StateFlow<List<AlertaEntity>> = repository.getAlerts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- NUEVAS FUNCIONES PARA DIARIO (Get y Delete) ---
    suspend fun getEntradaDiarioById(id: Long): EntradaDiarioEntity? {
        return repository.getEntradaDiarioById(id)
    }

    fun eliminarEntradaDiario(id: Long) {
        viewModelScope.launch {
            repository.eliminarEntradaDiario(id)
        }
    }

    // --- FUNCIÓN GUARDAR MODIFICADA (SOPORTA EDICIÓN) ---
    // Acepta 'id' opcional. Si es 0 crea nueva, si es > 0 actualiza.
    fun guardarEntradaDiario(bancalId: Long, tipo: String, desc: String, fecha: Long, id: Long = 0L) {
        viewModelScope.launch {
            repository.insertarEntradaDiario(
                EntradaDiarioEntity(
                    id = id,
                    bancalId = bancalId,
                    tipoAccion = tipo,
                    descripcion = desc,
                    fecha = fecha
                )
            )
        }
    }

    // --- RESTO DE FUNCIONES (INTACTAS) ---
    fun addAlert(title: String, desc: String, epochMillis: Long) = viewModelScope.launch { repository.insertAlert(AlertaEntity(title = title, description = desc, dateTimeEpochMillis = epochMillis)) }
    fun updateAlert(id: Long, title: String, desc: String, epochMillis: Long) = viewModelScope.launch { repository.updateAlert(AlertaEntity(id = id, title = title, description = desc, dateTimeEpochMillis = epochMillis)) }
    fun deleteAlert(id: Long) = viewModelScope.launch { repository.deleteAlert(AlertaEntity(id = id, title = "", description = "", dateTimeEpochMillis = 0)) }

    val productosFertilizante: Flow<List<ProductoEntity>> = repository.getProductos().map { list -> list.filter { it.categoria == ProductType.FERTILIZER.name } }
    val productosQuimicos: Flow<List<ProductoEntity>> = repository.getProductos().map { list -> list.filter { it.categoria == ProductType.CHEMICAL.name } }

    private val _apiSearchResults = MutableStateFlow<List<PerenualSpecies>>(emptyList())
    val apiSearchResults = _apiSearchResults.asStateFlow()

    fun buscarCultivoApi(query: String) { viewModelScope.launch { _apiSearchResults.value = repository.buscarCultivosOnline(query) } }
    fun limpiarResultadosBusqueda() { _apiSearchResults.value = emptyList() }

    fun crearNuevaJardinera(n: String, f: Int, c: Int) = viewModelScope.launch { repository.crearJardineraConBancales(n, f, c) }
    fun archivar(j: JardineraEntity) = viewModelScope.launch { repository.archivarJardinera(j) }
    fun desarchivar(j: JardineraEntity) = viewModelScope.launch { repository.desarchivarJardinera(j) }
    fun actualizarJardinera(j: JardineraEntity, n: String, f: Int, c: Int) = viewModelScope.launch { repository.actualizarJardinera(j.copy(nombre = n, filas = f, columnas = c)) }
    fun regarJardineraCompleta(jId: Long) = viewModelScope.launch { repository.regarTodaLaJardinera(jId) }
    fun getBancales(id: Long) = repository.getBancales(id)
    suspend fun getBancalById(id: Long) = repository.getBancalById(id)
    fun toggleBancal(id: Long, f: Boolean) = viewModelScope.launch { repository.setEstadoFuncionalBancal(id, f) }
    fun plantar(bId: Long, apiId: Int) = viewModelScope.launch { repository.plantarEnBancal(bId, apiId) }
    fun cosechar(bId: Long) = viewModelScope.launch { repository.cosecharBancal(bId) }
    fun registrarRiego(bId: Long, litros: Double) = viewModelScope.launch { repository.registrarRiego(bId, litros) }
    fun aplicarTratamiento(bId: Long, p: ProductoEntity, cant: Double, tipo: String) = viewModelScope.launch { repository.registrarTratamiento(bId, p.id, cant, tipo) }
    fun getProductos() = repository.getProductos()
    suspend fun getProductoById(id: Long) = repository.getProductoById(id)
    fun eliminarProducto(id: Long) = viewModelScope.launch { repository.eliminarProducto(id) }
    fun updateStock(id: Long, n: Double) = viewModelScope.launch { repository.getProductoById(id)?.let { repository.insertarProducto(it.copy(stock = n)) } }
    fun guardarProducto(id: Long, n: String, c: String, s: Double, perenualId: Int? = null, imagenUrl: String? = null, nombreCientifico: String? = null, notas: String? = null) {
        viewModelScope.launch { repository.insertarProducto(ProductoEntity(id = id, nombre = n, categoria = c, stock = s, perenualId = perenualId, imagenUrl = imagenUrl, nombreCientifico = nombreCientifico, notasCultivo = notas)) }
    }
    fun getHistorial(id: Long) = repository.getHistorialBancal(id)
    fun getInfoExtendida(perenualId: Int?) = if (perenualId == null) null else repository.getFichaCompleta(perenualId)
    fun toggleFavorito(jardinera: JardineraEntity) = viewModelScope.launch {
        repository.actualizarJardinera(jardinera.copy(esFavorita = !jardinera.esFavorita))
    }
}