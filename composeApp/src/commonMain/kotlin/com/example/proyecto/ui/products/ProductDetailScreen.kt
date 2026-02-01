package com.example.proyecto.ui.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.proyecto.data.database.entity.ProductoEntity
import com.example.proyecto.domain.model.ProductType
import com.example.proyecto.ui.garden.GardenViewModel
import com.example.proyecto.ui.navigation.AppScreens
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.RedDanger
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: String,
    viewModel: GardenViewModel = koinViewModel()
) {
    val idLong = productId.toLongOrNull() ?: 0L
    var producto by remember { mutableStateOf<ProductoEntity?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(idLong) {
        producto = viewModel.getProductoById(idLong)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                    ) { Icon(Icons.Rounded.ArrowBack, null, tint = Color.White) }
                },
                actions = {
                    // --- MENÚ DE TRES PUNTOS ---
                    Box(modifier = Modifier.padding(8.dp)) {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        ) { Icon(Icons.Default.MoreVert, null, tint = Color.White) }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Editar Producto") },
                                leadingIcon = { Icon(Icons.Default.Edit, null) },
                                onClick = {
                                    showMenu = false
                                    navController.navigate(AppScreens.createEditProductRoute(idLong))
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Eliminar", color = RedDanger) },
                                leadingIcon = { Icon(Icons.Default.Delete, null, tint = RedDanger) },
                                onClick = {
                                    showMenu = false
                                    showDeleteConfirm = true
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        if (producto == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            val item = producto!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // --- CABECERA VISUAL ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    if (!item.imagenUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = item.imagenUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Degradado oscuro para que se vean los iconos
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.verticalGradient(colors = listOf(Color.Black.copy(0.4f), Color.Transparent, Color.Black.copy(0.6f))))
                        )
                    } else {
                        // Fondo por defecto si no hay imagen
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.verticalGradient(colors = listOf(GreenPrimary, GreenPrimary.copy(alpha = 0.6f))))
                        )
                    }

                    // Título e icono central
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(24.dp)
                            .padding(bottom = 40.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = GreenPrimary,
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(getIconForProductType(item.categoria), null, tint = Color.White)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = item.nombre,
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (!item.nombreCientifico.isNullOrBlank()) {
                            Text(
                                text = item.nombreCientifico,
                                style = MaterialTheme.typography.titleMedium,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                // --- TARJETA DE CONTENIDO ---
                Column(
                    modifier = Modifier
                        .offset(y = (-30).dp)
                        .padding(horizontal = 16.dp)
                ) {
                    ElevatedCard(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(24.dp)) {
                            // GRID DE DATOS TÉCNICOS
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                DetailInfoItem(Icons.Default.Category, "Categoría", item.categoria)
                                DetailInfoItem(Icons.Default.Inventory, "Stock", "${item.stock}")
                            }

                            Spacer(Modifier.height(20.dp))

                            if (item.perenualId != null) {
                                DetailInfoItem(Icons.Default.Link, "ID Catálogo", "#${item.perenualId}")
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "Notas y Cuidados",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = item.notasCultivo?.ifBlank { "Sin notas registradas." } ?: "Sin notas registradas.",
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 28.sp,
                            modifier = Modifier.padding(20.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(Modifier.height(100.dp))
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            icon = { Icon(Icons.Rounded.DeleteForever, null, tint = RedDanger) },
            title = { Text("¿Eliminar producto?") },
            text = { Text("Se perderá el registro de inventario permanentemente.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.eliminarProducto(idLong); navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDanger)
                ) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun DetailInfoItem(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(GreenPrimary.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun getIconForProductType(category: String): ImageVector {
    return try {
        when (ProductType.valueOf(category)) {
            ProductType.SEED -> Icons.Default.Grass
            ProductType.FERTILIZER -> Icons.Default.Science
            ProductType.CHEMICAL -> Icons.Default.BugReport
            ProductType.TOOL -> Icons.Default.Build
            else -> Icons.Default.Inventory
        }
    } catch (e: Exception) {
        Icons.Default.Inventory
    }
}