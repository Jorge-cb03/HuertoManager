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
import com.example.proyecto.domain.model.Producto
import com.example.proyecto.domain.model.ProductType
import com.example.proyecto.ui.HuertaCard
import com.example.proyecto.ui.navigation.AppScreens
import com.example.proyecto.ui.theme.RedDanger
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

@Composable
fun ProductsScreen(
    navController: NavController,
    viewModel: ProductsViewModel = viewModel { ProductsViewModel(AppModule.huertaRepository) }
) {
    val inventory by viewModel.inventory.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<Producto?>(null) }

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
                contentPadding = PaddingValues(bottom = 100.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(inventory) { item ->
                    InventoryCard(
                        item = item,
                        onClick = { navController.navigate(AppScreens.createProductDetailRoute(item.id)) },
                        onEdit = { navController.navigate(AppScreens.createProductDetailRoute(item.id)) },
                        onDelete = {
                            itemToDelete = item
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }

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

    if (showDeleteDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(Res.string.dialog_delete_slot_title)) },
            text = { Text("¿Estás seguro de que quieres eliminar ${itemToDelete?.nombre} del inventario?") },
            confirmButton = {
                Button(
                    onClick = {
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
    item: Producto,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val typeEnum = try {
        ProductType.valueOf(item.tipo)
    } catch (e: Exception) {
        ProductType.OTRO
    }

    // CORREGIDO: Solo usamos los valores válidos de ProductType
    val icon = when (typeEnum) {
        ProductType.HERRAMIENTA -> Icons.Filled.Build
        ProductType.SEMILLA -> Icons.Filled.Grain
        ProductType.FERTILIZANTE -> Icons.Filled.Science
        ProductType.OTRO -> Icons.Filled.Inventory2
    }

    val tagColor = when(typeEnum) {
        ProductType.HERRAMIENTA -> Color(0xFF90CAF9)
        ProductType.SEMILLA -> Color(0xFFA5D6A7)
        ProductType.FERTILIZANTE -> Color(0xFFFFCC80)
        ProductType.OTRO -> Color.Gray
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
                    item.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    item.descripcion,
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
                        item.tipo,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = tagColor
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    item.cantidad,
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