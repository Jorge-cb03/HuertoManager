package com.example.proyecto.ui.products

import androidx.compose.foundation.background
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
import com.example.proyecto.ui.HuertaCard
import com.example.proyecto.ui.navigation.AppScreens
import com.example.proyecto.ui.theme.RedDanger
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

enum class ProductType { TOOL, SEED, CHEMICAL, FERTILIZER, OTHER }

data class InventoryItem(
    val id: String,
    val name: String,
    val type: ProductType,
    val quantity: String,
    val description: String?
)

@Composable
fun ProductsScreen(navController: NavController) {
    // Convertimos la lista a MutableState para poder borrar elementos visualmente
    val initialInventory = listOf(
        InventoryItem("1", "Pala de Mano", ProductType.TOOL, "1 ud", "Herramienta básica"),
        InventoryItem("2", "Semillas Tomate", ProductType.SEED, "10 sobres", "Variedad Cherry"),
        InventoryItem("3", "Semillas Lechuga", ProductType.SEED, "50g", "Romana"),
        InventoryItem("4", "Fertilizante NPK", ProductType.CHEMICAL, "2.5 L", "Crecimiento"),
        InventoryItem("5", "Tijeras de Podar", ProductType.TOOL, "2 uds", "Acero inoxidable")
    )
    var inventory by remember { mutableStateOf(initialInventory) }

    // ESTADOS PARA EL DIÁLOGO DE BORRADO
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

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(inventory) { item ->
                InventoryCard(
                    item = item,
                    onClick = {
                        navController.navigate(AppScreens.createProductDetailRoute(item.id))
                    },
                    onEdit = {
                        // Por ahora navegamos al detalle al editar, o podrías crear una ruta 'edit_product/{id}'
                        navController.navigate(AppScreens.createProductDetailRoute(item.id))
                    },
                    onDelete = {
                        // PREPARAMOS EL BORRADO: Guardamos el item y mostramos diálogo
                        itemToDelete = item
                        showDeleteDialog = true
                    }
                )
            }

            item {
                Button(
                    onClick = { navController.navigate(AppScreens.AddProduct) },
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(50.dp),
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
        }
    }

    // --- DIÁLOGO DE CONFIRMACIÓN DE BORRADO ---
    if (showDeleteDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(Res.string.dialog_delete_slot_title)) }, // Reutilizamos título "¿Eliminar?"
            text = { Text("¿Estás seguro de que quieres eliminar ${itemToDelete?.name} del inventario?") },
            confirmButton = {
                Button(
                    onClick = {
                        // ACCIÓN DE BORRAR
                        inventory = inventory.filter { it.id != itemToDelete!!.id }
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
                Text(item.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(item.description ?: "", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary, maxLines = 1)
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = tagColor.copy(alpha = 0.2f),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
                ) {
                    Text(item.type.name, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = tagColor)
                }
                Spacer(Modifier.height(4.dp))
                Text(item.quantity, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            }

            Spacer(Modifier.width(8.dp))

            Box {
                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Filled.MoreVert, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
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