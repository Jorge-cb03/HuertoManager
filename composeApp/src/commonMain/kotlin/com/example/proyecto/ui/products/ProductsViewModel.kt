package com.example.proyecto.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.repository.HuertaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductsViewModel(
    private val repository: HuertaRepository
) : ViewModel() {

    // Lista reactiva real desde Room
    val inventory: StateFlow<List<InventoryItem>> = repository.productos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        repository.startSync()
    }

    fun deleteProduct(id: String) {
        viewModelScope.launch {
            repository.borrarProducto(id)
        }
    }

    // Función para crear (la usaremos desde AddProductScreen más tarde)
    fun addProduct(name: String, type: ProductType, quantity: String, desc: String) {
        viewModelScope.launch {
            repository.crearProducto(name, type.name, quantity, desc)
        }
    }
}