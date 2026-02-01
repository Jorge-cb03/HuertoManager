package com.example.proyecto.ui.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.data.database.entity.ProductoEntity
import com.example.proyecto.ui.HuertaCard
import com.example.proyecto.ui.garden.GardenViewModel
import com.example.proyecto.ui.navigation.AppScreens
import com.example.proyecto.ui.theme.GreenPrimary
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(navController: NavController, viewModel: GardenViewModel = koinViewModel()) {
    val productosState = viewModel.getProductos().collectAsState(initial = emptyList())
    val productos = productosState.value
    var searchQuery by remember { mutableStateOf("") }

    val filteredProducts = productos.filter { it.nombre.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(AppScreens.AddProduct) }, containerColor = GreenPrimary) { Icon(Icons.Default.Add, null, tint = Color.White) }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(horizontal = 20.dp)) {
            Text(text = "Inventario", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                placeholder = { Text("Buscar...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )

            if (filteredProducts.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Vacío", color = Color.Gray) }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredProducts) { producto ->
                        ProductItemCard(
                            producto = producto,
                            onDelta = { viewModel.updateStock(producto.id, producto.stock + it) },
                            onDelete = { viewModel.eliminarProducto(producto.id) },
                            onEdit = {
                                // FIX: Usamos la función que añadimos en AppScreens
                                navController.navigate(AppScreens.createProductDetailRoute(producto.id.toString()))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItemCard(producto: ProductoEntity, onEdit: () -> Unit, onDelta: (Double) -> Unit, onDelete: () -> Unit) {
    HuertaCard {
        Row(modifier = Modifier.fillMaxWidth().clickable { onEdit() }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(60.dp), shape = MaterialTheme.shapes.medium, color = GreenPrimary.copy(alpha = 0.1f)) { Icon(Icons.Default.Inventory, null, tint = GreenPrimary, modifier = Modifier.padding(12.dp)) }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold)
                Text("Stock: ${producto.stock}", color = GreenPrimary, fontWeight = FontWeight.Bold)
            }
            Row {
                IconButton(onClick = { onDelta(-1.0) }) { Icon(Icons.Default.RemoveCircleOutline, null) }
                IconButton(onClick = { onDelta(1.0) }) { Icon(Icons.Default.AddCircleOutline, null, tint = GreenPrimary) }
            }
        }
    }
}