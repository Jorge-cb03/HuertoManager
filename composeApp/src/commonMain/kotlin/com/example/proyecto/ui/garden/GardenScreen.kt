package com.example.proyecto.ui.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.data.database.entity.BancalEntity
import com.example.proyecto.ui.StatusPill
import com.example.proyecto.ui.home.ShortcutManager
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.RedDanger
import org.jetbrains.compose.resources.stringResource
import huertomanager.composeapp.generated.resources.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenScreen(
    navController: NavController,
    initialGardenId: Long = 0L,
    viewModel: GardenViewModel = koinViewModel()
) {
    val jardineras by viewModel.jardineras.collectAsState()
    var currentGardenIndex by remember { mutableStateOf(0) }

    // SINCRONIZACIÓN: Si venimos por ID, buscamos la posición real en la lista
    LaunchedEffect(jardineras, initialGardenId) {
        if (initialGardenId != 0L) {
            val index = jardineras.indexOfFirst { it.id == initialGardenId }
            if (index != -1) currentGardenIndex = index
        }
    }

    val currentJardinera = jardineras.getOrNull(currentGardenIndex)
    val bancales by produceState<List<BancalEntity>>(emptyList(), currentJardinera?.id) {
        currentJardinera?.let { viewModel.getBancales(it.id).collect { value = it } }
    }

    var showAddGardenDialog by remember { mutableStateOf(false) }
    var showGardenMenu by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf("") }
    var selectedSlotIdForPlanting by remember { mutableStateOf<Long?>(null) }
    var showPlantSelector by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(currentJardinera?.nombre ?: "Mi Huerta") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } },
                actions = {
                    currentJardinera?.let { jardinera ->
                        val isPinned = ShortcutManager.pinnedGardenIds.contains(jardinera.id)
                        IconButton(onClick = {
                            if (isPinned) ShortcutManager.pinnedGardenIds.remove(jardinera.id)
                            else ShortcutManager.pinnedGardenIds.add(jardinera.id)
                        }) {
                            Icon(
                                imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                contentDescription = null,
                                tint = if (isPinned) GreenPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Box {
                        IconButton(onClick = { showGardenMenu = true }) { Icon(Icons.Filled.MoreVert, null) }
                        DropdownMenu(expanded = showGardenMenu, onDismissRequest = { showGardenMenu = false }) {
                            DropdownMenuItem(text = { Text("Nueva Jardinera") }, leadingIcon = { Icon(Icons.Default.Add, null) }, onClick = { showGardenMenu = false; showAddGardenDialog = true })
                            DropdownMenuItem(text = { Text("Eliminar", color = RedDanger) }, leadingIcon = { Icon(Icons.Default.Delete, null, tint = RedDanger) }, onClick = { showGardenMenu = false; currentJardinera?.let { viewModel.archivar(it) } })
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            if (currentJardinera == null) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Button(onClick = { showAddGardenDialog = true }) { Text("Crear mi primera jardinera") }
                }
            } else {
                // CONTROLES DE FILAS Y COLUMNAS
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(Modifier.padding(12.dp).fillMaxWidth(), Arrangement.SpaceEvenly, Alignment.CenterVertically) {
                        DimensionControl("Filas", currentJardinera.filas) { delta ->
                            viewModel.actualizarJardinera(currentJardinera, currentJardinera.nombre, (currentJardinera.filas + delta).coerceAtLeast(1), currentJardinera.columnas)
                        }
                        DimensionControl("Columnas", currentJardinera.columnas) { delta ->
                            viewModel.actualizarJardinera(currentJardinera, currentJardinera.nombre, currentJardinera.filas, (currentJardinera.columnas + delta).coerceAtLeast(1))
                        }
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(currentJardinera.columnas),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(bancales.sortedWith(compareBy({ it.fila }, { it.columna }))) { bancal ->
                        BancalSlotCard(
                            bancal = bancal,
                            onToggleVisibility = { viewModel.toggleBancal(bancal.id, !bancal.esFuncional) },
                            onClick = {
                                if (bancal.perenualId == null) {
                                    selectedSlotIdForPlanting = bancal.id
                                    showPlantSelector = true
                                } else {
                                    navController.navigate("garden_slot_detail/${bancal.id}")
                                }
                            }
                        )
                    }
                }

                Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    IconButton(onClick = { if (currentGardenIndex > 0) currentGardenIndex-- }, enabled = currentGardenIndex > 0) { Icon(Icons.Filled.ChevronLeft, null) }
                    Text("${currentGardenIndex + 1} / ${jardineras.size}", fontWeight = FontWeight.Bold)
                    IconButton(onClick = { if (currentGardenIndex < jardineras.size - 1) currentGardenIndex++ }, enabled = currentGardenIndex < jardineras.size - 1) { Icon(Icons.Filled.ChevronRight, null) }
                }
            }
        }
    }

    if (showAddGardenDialog) {
        AlertDialog(
            onDismissRequest = { showAddGardenDialog = false },
            title = { Text("Nueva Jardinera") },
            text = { OutlinedTextField(value = tempName, onValueChange = { tempName = it }, label = { Text("Nombre") }) },
            confirmButton = { Button(onClick = { if(tempName.isNotBlank()) viewModel.crearNuevaJardinera(tempName, 4, 2); showAddGardenDialog = false }) { Text("Crear") } }
        )
    }

    if (showPlantSelector) {
        AlertDialog(
            onDismissRequest = { showPlantSelector = false },
            title = { Text("Plantar") },
            text = {
                Column {
                    ListItem(headlineContent = { Text("Tomate Cherry") }, leadingContent = { Icon(Icons.Filled.Eco, null, tint = GreenPrimary) }, modifier = Modifier.clickable {
                        selectedSlotIdForPlanting?.let { viewModel.plantar(it, 2) }
                        showPlantSelector = false
                    })
                }
            },
            confirmButton = { TextButton(onClick = { showPlantSelector = false }) { Text("Cerrar") } }
        )
    }
}

@Composable
fun DimensionControl(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 10.sp, color = Color.Gray)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onValueChange(-1) }) { Icon(Icons.Default.RemoveCircleOutline, null) }
            Text("$value", fontWeight = FontWeight.Bold)
            IconButton(onClick = { onValueChange(1) }) { Icon(Icons.Default.AddCircleOutline, null) }
        }
    }
}

@Composable
fun BancalSlotCard(bancal: BancalEntity, onToggleVisibility: () -> Unit, onClick: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    val isEmpty = bancal.perenualId == null

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (!bancal.esFuncional) Color.Transparent else if (isEmpty) MaterialTheme.colorScheme.surfaceVariant.copy(0.4f) else MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().aspectRatio(1f).clickable(enabled = bancal.esFuncional) { onClick() }
            .then(if (!bancal.esFuncional) Modifier.border(1.dp, Color.Gray.copy(0.3f), RoundedCornerShape(12.dp)) else Modifier)
    ) {
        Box(Modifier.fillMaxSize()) {
            IconButton(onClick = { showMenu = true }, modifier = Modifier.align(Alignment.TopEnd).size(24.dp)) { Icon(Icons.Default.MoreVert, null, modifier = Modifier.size(14.dp), tint = Color.Gray) }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(text = { Text(if(bancal.esFuncional) "Ocultar" else "Mostrar") }, onClick = { showMenu = false; onToggleVisibility() })
            }
            if (bancal.esFuncional) {
                Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                    if (isEmpty) { Icon(Icons.Default.Add, null, tint = Color.Gray.copy(0.5f)) }
                    else { Icon(Icons.Default.Eco, null, tint = GreenPrimary); Text(bancal.nombreCultivo ?: "", fontSize = 10.sp, maxLines = 1) }
                }
            } else {
                Icon(Icons.Default.Block, null, tint = Color.Gray.copy(0.2f), modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}