package com.example.proyecto.ui.garden

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto.di.AppModule
import com.example.proyecto.domain.model.Bancal
import com.example.proyecto.domain.model.EstadoBancal
import com.example.proyecto.ui.StatusPill
import com.example.proyecto.ui.navigation.AppScreens
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.RedDanger
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenScreen(
    navController: NavController,
    viewModel: GardenViewModel = viewModel { GardenViewModel(AppModule.huertaRepository) }
) {
    val gardens by viewModel.gardenPages.collectAsState()
    val semillas by viewModel.semillasDisponibles.collectAsState()

    var currentGardenIndex by remember { mutableStateOf(0) }

    LaunchedEffect(gardens.size) {
        if (gardens.isNotEmpty()) {
            if (currentGardenIndex >= gardens.size) currentGardenIndex = gardens.size - 1
        } else {
            currentGardenIndex = 0
        }
    }

    val currentGarden = gardens.getOrNull(currentGardenIndex)

    var showPlantSelector by remember { mutableStateOf(false) }
    var selectedSlotIdForPlanting by remember { mutableStateOf("") }

    var showAddGardenDialog by remember { mutableStateOf(false) }
    var showDeleteGardenDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showResizeDialog by remember { mutableStateOf(false) }

    var tempName by remember { mutableStateOf("") }
    var newRowsInput by remember { mutableStateOf("") }
    var showGardenMenu by remember { mutableStateOf(false) }

    var actionToConfirm by remember { mutableStateOf<SlotAction?>(null) }
    var slotIdToConfirm by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentGarden == null) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (gardens.isEmpty()) {
                            Text("No tienes jardineras activas", color = Color.Gray)
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { tempName = ""; showAddGardenDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) {
                                Text("Crear mi primera jardinera")
                            }
                        } else {
                            CircularProgressIndicator()
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentGarden.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        val cols = if (currentGarden.columns > 0) currentGarden.columns else 2
                        val filas = currentGarden.slots.size / cols
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
                                text = { Row { Icon(Icons.Filled.AspectRatio, null); Spacer(Modifier.width(8.dp)); Text("Añadir Filas") } },
                                onClick = {
                                    showGardenMenu = false
                                    val cols = if (currentGarden.columns > 0) currentGarden.columns else 2
                                    val filasActuales = currentGarden.slots.size / cols
                                    newRowsInput = (filasActuales + 1).toString()
                                    showResizeDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Row { Icon(Icons.Filled.Delete, null, tint = RedDanger); Spacer(Modifier.width(8.dp)); Text("Borrar", color = RedDanger) } },
                                onClick = { showGardenMenu = false; showDeleteGardenDialog = true }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                val safeColumns = if (currentGarden.columns > 0) currentGarden.columns else 2

                LazyVerticalGrid(
                    columns = GridCells.Fixed(safeColumns),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(items = currentGarden.slots, key = { it.id }) { bancal ->
                        GardenBancalCard(
                            bancal = bancal,
                            onClick = {
                                if (bancal.estado == EstadoBancal.VACIO) {
                                    selectedSlotIdForPlanting = bancal.id
                                    showPlantSelector = true
                                } else {
                                    navController.navigate(AppScreens.createSlotDetailRoute(bancal.id))
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
                if (gardens.size > 1) {
                    Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 5.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (currentGardenIndex > 0) currentGardenIndex-- }, enabled = currentGardenIndex > 0) { Icon(Icons.Filled.ChevronLeft, null) }
                        Text("${currentGardenIndex + 1} / ${gardens.size}", fontWeight = FontWeight.Bold)
                        IconButton(onClick = { if (currentGardenIndex < gardens.size - 1) currentGardenIndex++ }, enabled = currentGardenIndex < gardens.size - 1) { Icon(Icons.Filled.ChevronRight, null) }
                    }
                }
            }
        }

        // DIALOGOS
        if (showAddGardenDialog) {
            AlertDialog(
                onDismissRequest = { showAddGardenDialog = false },
                title = { Text("Nueva Jardinera") },
                text = { OutlinedTextField(value = tempName, onValueChange = { tempName = it }, label = { Text("Nombre") }) },
                confirmButton = { Button(onClick = { viewModel.crearJardinera(tempName, 4, 2); showAddGardenDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) { Text("Crear") } },
                dismissButton = { TextButton(onClick = { showAddGardenDialog = false }) { Text("Cancelar") } }
            )
        }

        if (showResizeDialog && currentGarden != null) {
            AlertDialog(
                onDismissRequest = { showResizeDialog = false },
                title = { Text("Añadir Filas") },
                text = { Column { Text("Filas totales:"); OutlinedTextField(value = newRowsInput, onValueChange = { newRowsInput = it }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)) } },
                confirmButton = { Button(onClick = { val newRows = newRowsInput.toIntOrNull(); val currentRows = if (currentGarden.columns > 0) currentGarden.slots.size / currentGarden.columns else 0; if (newRows != null && newRows > currentRows) { viewModel.redimensionarJardinera(currentGarden.id, currentRows, currentGarden.columns, newRows); showResizeDialog = false } }, colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) { Text("Actualizar") } },
                dismissButton = { TextButton(onClick = { showResizeDialog = false }) { Text("Cancelar") } }
            )
        }

        if (showDeleteGardenDialog && currentGarden != null) {
            AlertDialog(
                onDismissRequest = { showDeleteGardenDialog = false },
                title = { Text("Eliminar") },
                text = { Text("¿Estás seguro?") },
                confirmButton = { Button(onClick = { viewModel.borrarJardinera(currentGarden.id); showDeleteGardenDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = RedDanger)) { Text("Eliminar") } },
                dismissButton = { TextButton(onClick = { showDeleteGardenDialog = false }) { Text("Cancelar") } }
            )
        }

        if (actionToConfirm == SlotAction.HARVEST && slotIdToConfirm != null) {
            AlertDialog(
                onDismissRequest = { actionToConfirm = null },
                title = { Text("¿Cosechar?") },
                confirmButton = { Button(onClick = { viewModel.cosechar(slotIdToConfirm!!); actionToConfirm = null; slotIdToConfirm = null }, colors = ButtonDefaults.buttonColors(containerColor = RedDanger)) { Text("Cosechar") } },
                dismissButton = { TextButton(onClick = { actionToConfirm = null }) { Text("Cancelar") } }
            )
        }

        if (showPlantSelector) {
            AlertDialog(
                onDismissRequest = { showPlantSelector = false },
                icon = { Icon(Icons.Filled.Grass, null) },
                title = { Text("Sembrar") },
                text = { if (semillas.isEmpty()) Text("Sin semillas.", color = RedDanger) else { Column(Modifier.heightIn(max=300.dp)) { semillas.forEach { s -> ListItem(headlineContent={Text(s.nombre)}, leadingContent={Icon(Icons.Filled.Eco,null)}, modifier=Modifier.clickable{ viewModel.sembrar(selectedSlotIdForPlanting, s); showPlantSelector=false }); HorizontalDivider() } } } },
                confirmButton = { TextButton(onClick = { showPlantSelector = false }) { Text("Cancelar") } }
            )
        }
    }
}

enum class SlotAction { HARVEST }

@Composable
fun GardenBancalCard(bancal: Bancal, onClick: () -> Unit, onAction: (SlotAction) -> Unit) {
    val isEmpty = bancal.estado == EstadoBancal.VACIO
    var showMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isEmpty) MaterialTheme.colorScheme.surfaceVariant.copy(0.5f) else MaterialTheme.colorScheme.surface),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
    ) {
        Box(Modifier.fillMaxSize().padding(8.dp)) {
            Text("${bancal.indice + 1}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.4f), modifier = Modifier.align(Alignment.TopStart))
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                if (isEmpty) {
                    Icon(Icons.Filled.AddCircleOutline, null, tint = MaterialTheme.colorScheme.onSurface.copy(0.2f), modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Filled.Eco, null, tint = GreenPrimary, modifier = Modifier.size(32.dp))
                    Text(bancal.planta?.nombre ?: "Planta", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                    StatusPill("Sano")
                }
            }

            Box(Modifier.align(Alignment.TopEnd)) {
                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Filled.MoreVert, null, tint = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    if (!isEmpty) {
                        DropdownMenuItem(text = { Text("Cosechar") }, onClick = { showMenu = false; onAction(SlotAction.HARVEST) })
                    } else {
                        DropdownMenuItem(text = { Text("Vacío", color = Color.Gray) }, onClick = { }, enabled = false)
                    }
                }
            }
        }
    }
}