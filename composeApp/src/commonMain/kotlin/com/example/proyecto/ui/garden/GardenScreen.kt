package com.example.proyecto.ui.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.data.database.entity.BancalEntity
import com.example.proyecto.domain.model.ProductType
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.RedDanger
import org.koin.compose.viewmodel.koinViewModel
import coil3.compose.AsyncImage //
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip

// Enum para acciones internas de la UI
enum class SlotAction { HARVEST }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenScreen(
    navController: NavController,
    viewModel: GardenViewModel = koinViewModel()
) {
    // 1. Estados Globales (Base de Datos)
    val jardineras by viewModel.jardineras.collectAsState()

    // Obtenemos productos y filtramos semillas en memoria para el selector
    val productos by viewModel.getProductos().collectAsState(initial = emptyList())
    val semillas = remember(productos) {
        productos.filter { it.categoria == ProductType.SEED.name || it.categoria == "SEED" }
    }

    // 2. Control de Paginación de Jardineras
    var currentGardenIndex by remember { mutableStateOf(0) }

    // Asegurar índice válido si se borran jardineras
    LaunchedEffect(jardineras.size) {
        if (jardineras.isNotEmpty()) {
            if (currentGardenIndex >= jardineras.size) currentGardenIndex = jardineras.size - 1
        } else {
            currentGardenIndex = 0
        }
    }

    val currentGarden = jardineras.getOrNull(currentGardenIndex)

    // 3. Cargar Bancales de la Jardinera Actual
    val bancales by produceState<List<BancalEntity>>(initialValue = emptyList(), currentGarden?.id) {
        currentGarden?.let { jardinera ->
            viewModel.getBancales(jardinera.id).collect { value = it }
        }
    }

    // 4. Estados de Diálogos y UI
    var showPlantSelector by remember { mutableStateOf(false) }
    var selectedSlotIdForPlanting by remember { mutableStateOf<Long?>(null) }

    var showAddGardenDialog by remember { mutableStateOf(false) }
    var showDeleteGardenDialog by remember { mutableStateOf(false) }
    var showResizeDialog by remember { mutableStateOf(false) }

    var tempName by remember { mutableStateOf("") }
    var newRowsInput by remember { mutableStateOf("") }
    var showGardenMenu by remember { mutableStateOf(false) }

    var actionToConfirm by remember { mutableStateOf<SlotAction?>(null) }
    var slotIdToConfirm by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentGarden == null) {
                // VISTA VACÍA
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (jardineras.isEmpty()) {
                            Text("No tienes jardineras activas", color = Color.Gray)
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { tempName = ""; showAddGardenDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                            ) {
                                Text("Crear mi primera jardinera")
                            }
                        } else {
                            CircularProgressIndicator()
                        }
                    }
                }
            } else {
                // CABECERA JARDINERA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentGarden.nombre,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        val cols = if (currentGarden.columnas > 0) currentGarden.columnas else 2
                        val filas = currentGarden.filas
                        Text("$cols Col x $filas Filas", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                    }

                    Box {
                        IconButton(onClick = { showGardenMenu = true }) { Icon(Icons.Filled.MoreVert, null) }
                        DropdownMenu(expanded = showGardenMenu, onDismissRequest = { showGardenMenu = false }) {
                            DropdownMenuItem(
                                text = { Row { Icon(Icons.Filled.Add, null); Spacer(Modifier.width(8.dp)); Text("Nueva Jardinera") } },
                                onClick = { showGardenMenu = false; tempName = ""; showAddGardenDialog = true }
                            )
                            DropdownMenuItem(
                                text = { Row { Icon(Icons.Filled.AspectRatio, null); Spacer(Modifier.width(8.dp)); Text("Editar/Redimensionar") } },
                                onClick = {
                                    showGardenMenu = false
                                    newRowsInput = currentGarden.filas.toString()
                                    showResizeDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Row { Icon(Icons.Filled.Delete, null, tint = RedDanger); Spacer(Modifier.width(8.dp)); Text("Archivar/Borrar", color = RedDanger) } },
                                onClick = { showGardenMenu = false; showDeleteGardenDialog = true }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // GRID DE BANCALES
                val safeColumns = if (currentGarden.columnas > 0) currentGarden.columnas else 2

                LazyVerticalGrid(
                    columns = GridCells.Fixed(safeColumns),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    val sortedSlots = bancales.sortedWith(compareBy({ it.fila }, { it.columna }))

                    items(items = sortedSlots, key = { it.id }) { bancal ->
                        GardenBancalCard(
                            bancal = bancal,
                            onClick = {
                                // CORRECCIÓN 1: Usamos perenualId (Int?)
                                if (bancal.perenualId == null) {
                                    selectedSlotIdForPlanting = bancal.id
                                    showPlantSelector = true
                                } else {
                                    navController.navigate("garden_slot_detail/${bancal.id}")
                                }
                            },
                            onAction = { action ->
                                slotIdToConfirm = bancal.id
                                actionToConfirm = action
                            }
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // PAGINADOR
                if (jardineras.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { if (currentGardenIndex > 0) currentGardenIndex-- }, enabled = currentGardenIndex > 0) { Icon(Icons.Filled.ChevronLeft, null) }
                        Text("${currentGardenIndex + 1} / ${jardineras.size}", fontWeight = FontWeight.Bold)
                        IconButton(onClick = { if (currentGardenIndex < jardineras.size - 1) currentGardenIndex++ }, enabled = currentGardenIndex < jardineras.size - 1) { Icon(Icons.Filled.ChevronRight, null) }
                    }
                }
            }
        }

        // --- DIALOGOS ---

        // 1. Crear Jardinera
        if (showAddGardenDialog) {
            AlertDialog(
                onDismissRequest = { showAddGardenDialog = false },
                title = { Text("Nueva Jardinera") },
                text = { OutlinedTextField(value = tempName, onValueChange = { tempName = it }, label = { Text("Nombre") }) },
                confirmButton = {
                    Button(
                        onClick = { viewModel.crearNuevaJardinera(tempName, 4, 2); showAddGardenDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) { Text("Crear") }
                },
                dismissButton = { TextButton(onClick = { showAddGardenDialog = false }) { Text("Cancelar") } }
            )
        }

        // 2. Redimensionar
        if (showResizeDialog && currentGarden != null) {
            AlertDialog(
                onDismissRequest = { showResizeDialog = false },
                title = { Text("Editar Dimensiones") },
                text = {
                    Column {
                        Text("Filas totales:")
                        OutlinedTextField(
                            value = newRowsInput,
                            onValueChange = { newRowsInput = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newRows = newRowsInput.toIntOrNull() ?: currentGarden.filas
                            viewModel.actualizarJardinera(currentGarden, currentGarden.nombre, newRows, currentGarden.columnas)
                            showResizeDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) { Text("Actualizar") }
                },
                dismissButton = { TextButton(onClick = { showResizeDialog = false }) { Text("Cancelar") } }
            )
        }

        // 3. Borrar
        if (showDeleteGardenDialog && currentGarden != null) {
            AlertDialog(
                onDismissRequest = { showDeleteGardenDialog = false },
                title = { Text("Archivar Jardinera") },
                text = { Text("¿Seguro? Se ocultará de la vista principal.") },
                confirmButton = {
                    Button(
                        onClick = { viewModel.archivar(currentGarden); showDeleteGardenDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = RedDanger)
                    ) { Text("Archivar") }
                },
                dismissButton = { TextButton(onClick = { showDeleteGardenDialog = false }) { Text("Cancelar") } }
            )
        }

        // 4. Confirmar Cosecha
        if (actionToConfirm == SlotAction.HARVEST && slotIdToConfirm != null) {
            AlertDialog(
                onDismissRequest = { actionToConfirm = null },
                title = { Text("¿Cosechar?") },
                text = { Text("El bancal quedará vacío y se registrará en el diario.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.cosechar(slotIdToConfirm!!)
                            actionToConfirm = null
                            slotIdToConfirm = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RedDanger)
                    ) { Text("Cosechar") }
                },
                dismissButton = { TextButton(onClick = { actionToConfirm = null }) { Text("Cancelar") } }
            )
        }

        // 5. Selector de Semillas (Plantar)
        if (showPlantSelector) {
            AlertDialog(
                onDismissRequest = { showPlantSelector = false },
                icon = { Icon(Icons.Filled.Grass, null) },
                title = { Text("Sembrar") },
                text = {
                    if (semillas.isEmpty()) {
                        Text("No tienes semillas en el inventario.\nVe a Inventario > Añadir Producto.", color = RedDanger)
                    } else {
                        Column(Modifier.heightIn(max = 300.dp)) {
                            semillas.forEach { s ->
                                ListItem(
                                    headlineContent = { Text(s.nombre) },
                                    supportingContent = { Text("Stock: ${s.stock}") },
                                    leadingContent = { Icon(Icons.Filled.Eco, null) },
                                    modifier = Modifier.clickable {
                                        selectedSlotIdForPlanting?.let { bId ->
                                            // CORRECCIÓN 2: Usamos perenualId (Int)
                                            val pId = s.perenualId
                                            if (pId != null) {
                                                viewModel.plantar(bId, pId) // Pasamos Int
                                            } else {
                                                // Fallback o manejo de error si es semilla sin ID
                                            }
                                        }
                                        showPlantSelector = false
                                    }
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                },
                confirmButton = { TextButton(onClick = { showPlantSelector = false }) { Text("Cancelar") } }
            )
        }
    }
}

// COMPONENTE DE TARJETA INDIVIDUAL (Adaptado a BancalEntity con Perenual)
@Composable
fun GardenBancalCard(
    bancal: BancalEntity,
    onClick: () -> Unit,
    onAction: (SlotAction) -> Unit
) {
    // CORRECCIÓN 3: perenualId (Int?)
    val isEmpty = bancal.perenualId == null
    var showMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEmpty) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() }
    ) {
        Box(Modifier.fillMaxSize()) {
            // Fondo de imagen si existe
            if (!bancal.imagenUrl.isNullOrBlank()) {
                AsyncImage(
                    model = bancal.imagenUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Capa oscura para que se lea el texto
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)))
            }

            // Contenido
            Box(Modifier.fillMaxSize().padding(8.dp)) {
                Text(
                    "${bancal.fila}:${bancal.columna}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (!isEmpty) Color.White else MaterialTheme.colorScheme.onSurface.copy(0.4f),
                    modifier = Modifier.align(Alignment.TopStart)
                )

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isEmpty) {
                        Icon(
                            Icons.Filled.AddCircleOutline,
                            null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(0.2f),
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        // Si no hay imagen, mostramos icono
                        if (bancal.imagenUrl.isNullOrBlank()) {
                            Icon(
                                Icons.Filled.Eco,
                                null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Text(
                            text = bancal.nombreCultivo ?: "Planta",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White, // Texto blanco sobre fondo oscuro
                            maxLines = 1
                        )
                    }
                }

                if (!isEmpty) {
                    Box(Modifier.align(Alignment.TopEnd)) {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Filled.MoreVert, null, tint = Color.White)
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Cosechar") },
                                onClick = { showMenu = false; onAction(SlotAction.HARVEST) }
                            )
                        }
                    }
                }
            }
        }
    }
}