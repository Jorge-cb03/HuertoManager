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

    // --- STATE FLOWS (UI State) ---
    val jardineras: StateFlow<List<JardineraEntity>> = repository.getJardineras()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historialGeneral: StateFlow<List<EntradaDiarioEntity>> = repository.getTodoElHistorial()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- ALERTAS (PERSISTENCIA) - ESTO ES LO QUE TE FALTA ---
    val alerts: StateFlow<List<AlertEntity>> = repository.getAlerts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addAlert(title: String, desc: String, epochMillis: Long) = viewModelScope.launch {
        repository.insertAlert(AlertEntity(title = title, description = desc, dateTimeEpochMillis = epochMillis, isUrgent = false))
    }
    fun updateAlert(id: Long, title: String, desc: String, epochMillis: Long) = viewModelScope.launch {
        repository.updateAlert(AlertEntity(id = id, title = title, description = desc, dateTimeEpochMillis = epochMillis, isUrgent = false))
    }
    fun deleteAlert(id: Long) = viewModelScope.launch {
        repository.deleteAlert(AlertEntity(id = id, title = "", description = "", dateTimeEpochMillis = 0, isUrgent = false))
    }
    // --------------------------------------------------------

    // --- PRODUCTOS ---
    val productosFertilizante: Flow<List<ProductoEntity>> = repository.getProductos()
        .map { list -> list.filter { it.categoria == ProductType.FERTILIZER.name } }

    val productosQuimicos: Flow<List<ProductoEntity>> = repository.getProductos()
        .map { list -> list.filter { it.categoria == ProductType.CHEMICAL.name } }

    // --- BÚSQUEDA LOCAL ---
    private val _apiSearchResults = MutableStateFlow<List<PerenualSpecies>>(emptyList())
    val apiSearchResults = _apiSearchResults.asStateFlow()

    fun buscarCultivoApi(query: String) {
        viewModelScope.launch {
            _apiSearchResults.value = repository.buscarCultivosOnline(query)
        }
    }
    fun limpiarResultadosBusqueda() { _apiSearchResults.value = emptyList() }

    // --- CRUD ---
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
    fun registrarAccionRapida(id: Long, tipo: String) = viewModelScope.launch { repository.insertarEntradaDiario(EntradaDiarioEntity(bancalId = id, tipoAccion = tipo, descripcion = "$tipo rápida", fecha = System.currentTimeMillis())) }
    fun getProductos() = repository.getProductos()
    suspend fun getProductoById(id: Long) = repository.getProductoById(id)
    fun eliminarProducto(id: Long) = viewModelScope.launch { repository.eliminarProducto(id) }
    fun updateStock(id: Long, n: Double) = viewModelScope.launch { repository.getProductoById(id)?.let { repository.insertarProducto(it.copy(stock = n)) } }
    fun guardarProducto(id: Long, n: String, c: String, s: Double, perenualId: Int? = null, imagenUrl: String? = null, nombreCientifico: String? = null, notas: String? = null) {
        viewModelScope.launch { repository.insertarProducto(ProductoEntity(id = id, nombre = n, categoria = c, stock = s, perenualId = perenualId, imagenUrl = imagenUrl, nombreCientifico = nombreCientifico, notasCultivo = notas)) }
    }
    fun guardarEntradaDiario(bancalId: Long, tipo: String, desc: String, fecha: Long) { viewModelScope.launch { repository.insertarEntradaDiario(EntradaDiarioEntity(bancalId = bancalId, tipoAccion = tipo, descripcion = desc, fecha = fecha)) } }
    fun getHistorial(id: Long) = repository.getHistorialBancal(id)
    fun getInfoExtendida(perenualId: Int?) = if (perenualId == null) null else repository.getFichaCompleta(perenualId)
}