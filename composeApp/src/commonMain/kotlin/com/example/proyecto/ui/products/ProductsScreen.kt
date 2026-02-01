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
    // SOLUCIÓN: Eliminamos 'by' y usamos .value para evitar el error de inferencia de tipos
    val productosState = viewModel.getProductos().collectAsState(initial = emptyList())
    val productos = productosState.value

    var searchQuery by remember { mutableStateOf("") }

    // Filtro en tiempo real por nombre
    val filteredProducts = productos.filter {
        it.nombre.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AppScreens.AddProduct) },
                containerColor = GreenPrimary
            ) { Icon(Icons.Default.Add, contentDescription = "Añadir Producto", tint = Color.White) }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(horizontal = 20.dp)) {
            Text(
                text = "Mi Inventario",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // BARRA DE BÚSQUEDA
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                placeholder = { Text("Buscar semillas, abonos...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )

            if (filteredProducts.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron productos.", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filteredProducts) { producto ->
                        ProductItemCard(
                            producto = producto,
                            onDelta = { delta ->
                                // Actualiza el stock sumando el delta (1.0 o -1.0)
                                viewModel.updateStock(producto.id, producto.stock + delta)
                            },
                            onDelete = { viewModel.eliminarProducto(producto.id) },
                            onEdit = {
                                // Navegación a la pantalla de edición usando el ID del producto
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
fun ProductItemCard(
    producto: ProductoEntity,
    onEdit: () -> Unit,
    onDelta: (Double) -> Unit,
    onDelete: () -> Unit
) {
    // Determina la unidad de medida según la categoría
    val unidad = when (producto.categoria) {
        "FERTILIZER", "CHEMICAL" -> "kg/L"
        else -> "uds"
    }

    HuertaCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEdit() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono representativo
            Surface(
                Modifier.size(60.dp),
                shape = MaterialTheme.shapes.medium,
                color = GreenPrimary.copy(alpha = 0.1f)
            ) {
                Icon(Icons.Default.Eco, null, tint = GreenPrimary, modifier = Modifier.padding(12.dp))
            }

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(producto.categoria, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                // Color rojo si el stock es bajo
                Text(
                    text = "Stock: ${producto.stock} $unidad",
                    color = if (producto.stock < 5.0) Color.Red else GreenPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            // ACCIONES RÁPIDAS (Stock y Menú)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onDelta(-1.0) }) {
                    Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Restar", tint = Color.Gray)
                }
                IconButton(onClick = { onDelta(1.0) }) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = "Sumar", tint = GreenPrimary)
                }

                var expanded by remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar") },
                            onClick = {
                                expanded = false
                                onEdit()
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar", color = Color.Red) },
                            onClick = {
                                expanded = false
                                onDelete()
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                        )
                    }
                }
            }
        }
    }
}