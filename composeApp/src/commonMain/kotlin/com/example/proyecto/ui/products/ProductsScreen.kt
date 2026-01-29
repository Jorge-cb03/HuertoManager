package com.example.proyecto.ui.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto.domain.model.InventoryItem
import com.example.proyecto.domain.model.ProductType
import com.example.proyecto.ui.InventoryCard // IMPORTANTE: Importamos el nuevo componente
import com.example.proyecto.ui.garden.GardenViewModel
import com.example.proyecto.ui.navigation.AppScreens
import com.example.proyecto.ui.theme.GreenPrimary
import org.jetbrains.compose.resources.stringResource
import huertomanager.composeapp.generated.resources.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProductsScreen(navController: NavController, viewModel: GardenViewModel = koinViewModel()) {
    val inventory by viewModel.getProductos().collectAsState(initial = emptyList())

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AppScreens.AddProduct) },
                containerColor = GreenPrimary
            ) {
                Icon(Icons.Default.Add, null, tint = Color.White)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(20.dp))
            Text(
                stringResource(Res.string.products_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(20.dp))

            if (inventory.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("No hay productos en el inventario.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(inventory) { entity ->
                        InventoryCard(
                            item = InventoryItem(
                                id = entity.id.toString(),
                                name = entity.nombre,
                                type = if (entity.categoria == "SEMILLA") ProductType.SEED else ProductType.TOOL,
                                quantity = "${entity.stock} uds",
                                description = entity.categoria
                            ),
                            onClick = { navController.navigate(AppScreens.createProductDetailRoute(entity.id.toString())) },
                            onEdit = { /* Lógica de edición */ },
                            onDelete = { /* Lógica de borrado */ }
                        )
                    }
                }
            }
        }
    }
}