package com.example.proyecto.ui.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.proyecto.ui.garden.GardenViewModel
import com.example.proyecto.ui.navigation.AppScreens
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.RedDanger
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
            FloatingActionButton(
                onClick = { navController.navigate(AppScreens.AddProduct) },
                containerColor = GreenPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Producto")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            Text(
                text = "Mis Productos",
                style = MaterialTheme.typography.headlineMedium,
                color = GreenPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Buscador
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar producto...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(Modifier.height(16.dp))

            if (filteredProducts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay productos. ¡Añade uno!", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProducts) { producto ->
                        // WRAPPER PARA EL MENÚ Y EL CLIC
                        Box {
                            // USAMOS LA NUEVA TARJETA ESPECÍFICA PARA PRODUCTOS
                            ProductItemCard(
                                name = producto.nombre,
                                quantity = producto.stock,
                                type = producto.categoria,
                                onClick = {
                                    // IR AL DETALLE
                                    navController.navigate(AppScreens.createProductDetailRoute(producto.id.toString()))
                                },
                                // ACCIONES DE STOCK
                                onIncrease = {
                                    // Asume que tienes esta función en el ViewModel
                                    viewModel.updateStock(producto.id, producto.stock + 1)
                                },
                                onDecrease = {
                                    // Evitamos stock negativo
                                    val newStock = (producto.stock - 1).coerceAtLeast(0.0)
                                    viewModel.updateStock(producto.id, newStock)
                                }
                            )

                            // Menú rápido (Tres puntos)
                            var showMenu by remember { mutableStateOf(false) }

                            Box(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                                IconButton(
                                    onClick = { showMenu = true },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.MoreVert, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Editar") },
                                        leadingIcon = { Icon(Icons.Default.Edit, null) },
                                        onClick = {
                                            showMenu = false
                                            navController.navigate(AppScreens.createEditProductRoute(producto.id))
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Eliminar", color = RedDanger) },
                                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = RedDanger) },
                                        onClick = {
                                            showMenu = false
                                            viewModel.eliminarProducto(producto.id)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- COMPONENTE LOCAL: TARJETA DE PRODUCTO MEJORADA ---
@Composable
fun ProductItemCard(
    name: String,
    quantity: Double,
    type: String,
    onClick: () -> Unit,
    onIncrease: () -> Unit, // Nuevo callback
    onDecrease: () -> Unit  // Nuevo callback
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Un poco más de elevación
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            // Aumentamos el padding interno para hacer la tarjeta visualmente más grande
            modifier = Modifier.padding(16.dp)
        ) {
            // Icono según tipo (simplificado)
            val icon = when {
                type.contains("SEED", true) -> Icons.Default.Grass
                type.contains("FERTILIZER", true) -> Icons.Default.Science
                type.contains("TOOL", true) -> Icons.Default.Build
                else -> Icons.Default.Inventory
            }

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(28.dp) // Icono un poco más grande
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Nombre del producto
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                fontSize = 18.sp // Fuente un poco más grande
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- SECCIÓN DE CONTROL DE STOCK ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Botón Menos (desactivado si stock es 0)
                FilledIconButton(
                    onClick = onDecrease,
                    enabled = quantity > 0,
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Disminuir", modifier = Modifier.size(16.dp))
                }

                // Cantidad de Stock (Destacada)
                Text(
                    text = if (quantity % 1.0 == 0.0) quantity.toInt().toString() else quantity.toString(), // Muestra enteros sin decimales .0
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = GreenPrimary, // "Marca rojo" (Usamos el color primario de la marca)
                    modifier = Modifier.weight(1f) // Ocupa el espacio central
                )

                // Botón Más
                FilledIconButton(
                    onClick = onIncrease,
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = GreenPrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}