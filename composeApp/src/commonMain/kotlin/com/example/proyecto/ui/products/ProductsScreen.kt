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
import org.jetbrains.compose.resources.stringResource
import huertomanager.composeapp.generated.resources.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(navController: NavController, viewModel: GardenViewModel = koinViewModel()) {
    val productosState = viewModel.getProductos().collectAsState(initial = emptyList())
    val productos = productosState.value
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf<Long?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // --- CORRECCIÓN: Precarga string ---
    val msgDeleted = stringResource(Res.string.dialog_success_product_deleted)

    val filteredProducts = productos.filter { it.nombre.contains(searchQuery, ignoreCase = true) && it.stock > 0 }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AppScreens.AddProduct) },
                containerColor = GreenPrimary,
                contentColor = Color.White
            ) { Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.products_add_fab)) }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text(
                text = stringResource(Res.string.products_title),
                style = MaterialTheme.typography.headlineMedium,
                color = GreenPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text(stringResource(Res.string.products_search_hint)) },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(Modifier.height(16.dp))

            if (filteredProducts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(Res.string.products_empty), color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProducts) { producto ->
                        Box {
                            ProductItemCard(
                                name = producto.nombre,
                                quantity = producto.stock,
                                type = producto.categoria,
                                onClick = { navController.navigate(AppScreens.createProductDetailRoute(producto.id.toString())) },
                                onIncrease = { viewModel.updateStock(producto.id, producto.stock + 1) },
                                onDecrease = { viewModel.updateStock(producto.id, (producto.stock - 1).coerceAtLeast(0.0)) }
                            )

                            var showMenu by remember { mutableStateOf(false) }
                            Box(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.MoreVert, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
                                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                    DropdownMenuItem(text = { Text(stringResource(Res.string.btn_edit)) }, leadingIcon = { Icon(Icons.Default.Edit, null) }, onClick = { showMenu = false; navController.navigate(AppScreens.createEditProductRoute(producto.id)) })
                                    DropdownMenuItem(text = { Text(stringResource(Res.string.btn_delete), color = RedDanger) }, leadingIcon = { Icon(Icons.Default.Delete, null, tint = RedDanger) }, onClick = { showMenu = false; showDeleteConfirm = producto.id })
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirm != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            icon = { Icon(Icons.Default.Warning, null, tint = RedDanger) },
            title = { Text(stringResource(Res.string.product_delete_confirm)) },
            text = { Text(stringResource(Res.string.product_delete_msg)) },
            confirmButton = { Button(onClick = { viewModel.eliminarProducto(showDeleteConfirm!!); showDeleteConfirm = null; showSuccessDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = RedDanger)) { Text(stringResource(Res.string.btn_delete)) } },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = null }) { Text(stringResource(Res.string.btn_cancel)) } }
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text(stringResource(Res.string.dialog_success_title)) },
            text = { Text(msgDeleted) }, // VARIABLE
            confirmButton = { Button(onClick = { showSuccessDialog = false }) { Text(stringResource(Res.string.dialog_btn_ok)) } }
        )
    }
}

@Composable
fun ProductItemCard(name: String, quantity: Double, type: String, onClick: () -> Unit, onIncrease: () -> Unit, onDecrease: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp)) {
            val icon = when {
                type.contains("SEED", true) -> Icons.Default.Grass
                type.contains("FERTILIZER", true) -> Icons.Default.Science
                type.contains("TOOL", true) -> Icons.Default.Build
                else -> Icons.Default.Inventory
            }
            Icon(imageVector = icon, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                FilledIconButton(onClick = onDecrease, enabled = quantity > 0, modifier = Modifier.size(32.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) { Icon(Icons.Default.Remove, contentDescription = stringResource(Res.string.products_decrease), modifier = Modifier.size(16.dp)) }
                Text(text = if (quantity % 1.0 == 0.0) quantity.toInt().toString() else quantity.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = if (quantity <= 3.0) RedDanger else GreenPrimary, modifier = Modifier.weight(1f))
                FilledIconButton(onClick = onIncrease, modifier = Modifier.size(32.dp), colors = IconButtonDefaults.filledIconButtonColors(containerColor = GreenPrimary)) { Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.products_increase), tint = Color.White, modifier = Modifier.size(16.dp)) }
            }
        }
    }
}