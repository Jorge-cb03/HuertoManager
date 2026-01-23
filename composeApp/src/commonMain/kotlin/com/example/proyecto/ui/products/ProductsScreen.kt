package com.example.proyecto.ui.products

import androidx.compose.foundation.background
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto.di.AppModule
import com.example.proyecto.ui.HuertaCard
import com.example.proyecto.ui.navigation.AppScreens
import com.example.proyecto.ui.theme.RedDanger
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

// Definimos los modelos aquí mismo para que coincidan con lo que espera el Repositorio
enum class ProductType { TOOL, SEED, CHEMICAL, FERTILIZER, OTHER }

data class InventoryItem(
    val id: String,
    val name: String,
    val type: ProductType,
    val quantity: String,
    val description: String?
)

@Composable
fun ProductsScreen(
    navController: NavController,
    // Inyectamos el ViewModel conectado al Repositorio (Room + Firebase)
    viewModel: ProductsViewModel = viewModel { ProductsViewModel(AppModule.huertaRepository) }
) {
    // 1. OBSERVAMOS LA LISTA REAL (Reactiva)
    val inventory by viewModel.inventory.collectAsState()

    // Estados para el diálogo de borrado
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<InventoryItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Text(
            stringResource(Res.string.products_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(20.dp))

        // Si la lista está vacía, mostramos un aviso
        if (inventory.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes productos. ¡Añade uno!", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                // Dejamos espacio abajo para el botón y el BottomBar
                contentPadding = PaddingValues(bottom = 100.dp),
                modifier = Modifier.weight(1f) // Ocupa el espacio restante
            ) {
                items(inventory) { item ->
                    InventoryCard(
                        item = item,
                        onClick = {
                            navController.navigate(AppScreens.createProductDetailRoute(item.id))
                        },
                        onEdit = {
                            navController.navigate(AppScreens.createProductDetailRoute(item.id))
                        },
                        onDelete = {
                            // Guardamos el item y abrimos diálogo
                            itemToDelete = item
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }

        // BOTÓN DE AÑADIR (Siempre visible abajo)
        Button(
            onClick = { navController.navigate(AppScreens.AddProduct) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(Icons.Filled.Add, null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(Res.string.products_add_btn))
        }
    }

    // --- DIÁLOGO DE CONFIRMACIÓN DE BORRADO ---
    if (showDeleteDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(Res.string.dialog_delete_slot_title)) },
            text = { Text("¿Estás seguro de que quieres eliminar ${itemToDelete?.name} del inventario?") },
            confirmButton = {
                Button(
                    onClick = {
                        // ACCIÓN REAL DE BORRAR (ViewModel -> Repo -> Firebase -> Room)
                        viewModel.deleteProduct(itemToDelete!!.id)
                        showDeleteDialog = false
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDanger)
                ) {
                    Text(stringResource(Res.string.dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(Res.string.dialog_cancel))
                }
            }
        )
    }
}

@Composable
fun InventoryCard(
    item: InventoryItem,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val icon = when (item.type) {
        ProductType.TOOL -> Icons.Filled.Build
        ProductType.SEED -> Icons.Filled.Grain
        ProductType.CHEMICAL -> Icons.Filled.Science
        ProductType.FERTILIZER -> Icons.Filled.Science
        ProductType.OTHER -> Icons.Filled.Inventory2
    }

    val tagColor = when(item.type) {
        ProductType.TOOL -> Color(0xFF90CAF9)
        ProductType.SEED -> Color(0xFFA5D6A7)
        ProductType.CHEMICAL -> Color(0xFFEF9A9A)
        ProductType.FERTILIZER -> Color(0xFFFFCC80)
        else -> Color.Gray
    }

    var showMenu by remember { mutableStateOf(false) }

    HuertaCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    item.description ?: "",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = tagColor.copy(alpha = 0.2f),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
                ) {
                    Text(
                        item.type.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = tagColor
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    item.quantity,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(Modifier.width(8.dp))

            Box {
                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Filled.MoreVert,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.menu_edit)) },
                        leadingIcon = { Icon(Icons.Filled.Edit, null) },
                        onClick = { showMenu = false; onEdit() }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.menu_delete), color = RedDanger) },
                        leadingIcon = { Icon(Icons.Filled.Delete, null, tint = RedDanger) },
                        onClick = { showMenu = false; onDelete() }
                    )
                }
            }
        }
    }
}