package com.example.proyecto.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.repository.HuertaRepository
import com.example.proyecto.domain.model.Producto // Usamos el nuevo modelo
import com.example.proyecto.domain.model.ProductType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val repository: HuertaRepository
) : ViewModel() {

    // Ahora la lista es de 'Producto', y 'repository.productos' ya existe
    val inventory: StateFlow<List<Producto>> = repository.productos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        repository.startSync()
    }

    fun deleteProduct(id: String) {
        viewModelScope.launch {
            repository.borrarProducto(id)
        }
    }

    fun addProduct(name: String, type: ProductType, quantity: String, desc: String) {
        viewModelScope.launch {
            repository.crearProducto(name, type.name, quantity, desc)
        }
    }
}