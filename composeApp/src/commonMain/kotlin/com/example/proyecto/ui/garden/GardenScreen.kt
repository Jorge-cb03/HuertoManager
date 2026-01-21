package com.example.proyecto.ui.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.ui.StatusPill
import com.example.proyecto.ui.navigation.AppScreens
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.RedDanger
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

// --- MODELOS DE DATOS ---
data class GardenSlot(
    val id: String,
    val positionName: String,
    val contentName: String?,
    val status: String?,
    val icon: ImageVector?,
    val isVisible: Boolean = true
)

data class GardenPage(
    val id: String,
    val name: String,
    val columns: Int,
    val slots: List<GardenSlot>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenScreen(navController: NavController) {
    var currentGardenIndex by remember { mutableStateOf(0) }
    var showPlantSelector by remember { mutableStateOf(false) }
    var selectedSlotIdForPlanting by remember { mutableStateOf("") }

    // Estado para renombrar jardinera
    var showRenameDialog by remember { mutableStateOf(false) }
    var tempGardenName by remember { mutableStateOf("") }

    // --- DATOS INICIALES (4x2 por defecto) ---
    var gardens by remember {
        mutableStateOf(
            listOf(
                GardenPage(
                    id = "1",
                    name = "Invernadero",
                    columns = 2,
                    slots = List(8) { idx ->
                        GardenSlot(
                            id = "s$idx",
                            positionName = "${idx+1}",
                            contentName = if(idx==0) "Tomates" else null,
                            status = if(idx==0) "Sano" else null,
                            icon = if(idx==0) Icons.Filled.Eco else null
                        )
                    }
                ),
                GardenPage(
                    id = "2",
                    name = "Terraza",
                    columns = 2,
                    slots = List(8) { idx ->
                        GardenSlot("t$idx", "${idx+1}", null, null, null)
                    }
                )
            )
        )
    }

    val currentGarden = gardens[currentGardenIndex]

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- CABECERA ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // TÍTULO CON DETECCIÓN DE DOBLE CLIC
                    Text(
                        text = currentGarden.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    tempGardenName = currentGarden.name // Cargar nombre actual
                                    showRenameDialog = true // Abrir diálogo
                                }
                            )
                        }
                    )
                    Text(
                        text = "${currentGarden.columns} ${stringResource(Res.string.garden_columns)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                // BOTÓN "NUEVA JARDINERA"
                Surface(
                    onClick = {
                        val newId = (gardens.size + 1).toString()
                        val newPage = GardenPage(
                            id = newId,
                            name = "Nueva Jardinera $newId",
                            columns = 2, // Siempre nace como 4x2 (8 huecos, 2 columnas)
                            slots = List(8) { idx ->
                                GardenSlot("n${newId}_$idx", "${idx+1}", null, null, null)
                            }
                        )
                        gardens = gardens + newPage
                        currentGardenIndex = gardens.size - 1
                    },
                    shape = CircleShape,
                    color = GreenPrimary,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Add, null, tint = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // --- CONTROLES DE DIMENSIÓN ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // CONTROL COLUMNAS
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (currentGarden.columns > 1) {
                                updateGarden(gardens, currentGardenIndex) { it.copy(columns = it.columns - 1) }
                                    .also { gardens = it }
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) { Icon(Icons.Filled.Remove, null) }

                    Text(
                        "${currentGarden.columns} Col",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        fontSize = 14.sp
                    )

                    IconButton(
                        onClick = {
                            // Lógica para añadir columna manteniendo filas rectangulares
                            val currentRows = (currentGarden.slots.size + currentGarden.columns - 1) / currentGarden.columns
                            val totalSlotsNeeded = currentRows * (currentGarden.columns + 1)
                            val slotsToAddCount = totalSlotsNeeded - currentGarden.slots.size

                            val newSlots = List(slotsToAddCount) {
                                GardenSlot(
                                    id = "${currentGarden.id}_extra_${System.currentTimeMillis()}_$it",
                                    positionName = "${currentGarden.slots.size + it + 1}",
                                    contentName = null, status = null, icon = null, isVisible = true
                                )
                            }

                            updateGarden(gardens, currentGardenIndex) {
                                it.copy(columns = it.columns + 1, slots = it.slots + newSlots)
                            }.also { gardens = it }
                        },
                        modifier = Modifier.size(32.dp)
                    ) { Icon(Icons.Filled.Add, null) }
                }

                // BOTÓN AÑADIR FILA
                Button(
                    onClick = {
                        val newSlots = List(currentGarden.columns) {
                            GardenSlot(
                                id = "${currentGarden.id}_${System.currentTimeMillis()}_$it",
                                positionName = "${currentGarden.slots.size + it + 1}",
                                contentName = null, status = null, icon = null, isVisible = true
                            )
                        }
                        updateGarden(gardens, currentGardenIndex) { it.copy(slots = it.slots + newSlots) }
                            .also { gardens = it }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Filled.TableRows, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(Res.string.garden_add_row), fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            // --- GRID DE JARDINERA ---
            LazyVerticalGrid(
                columns = GridCells.Fixed(currentGarden.columns),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(currentGarden.slots) { slot ->
                    if (slot.isVisible) {
                        GardenSlotCard(
                            slot = slot,
                            onClick = {
                                if (slot.contentName == null) {
                                    selectedSlotIdForPlanting = slot.id
                                    showPlantSelector = true
                                } else {
                                    navController.navigate(AppScreens.createSlotDetailRoute(slot.positionName))
                                }
                            },
                            onAction = { action ->
                                when(action) {
                                    SlotAction.HARVEST -> {
                                        updateSlot(gardens, currentGardenIndex, slot.id) { it.copy(contentName = null, status = null, icon = null) }
                                            .also { gardens = it }
                                    }
                                    SlotAction.HIDE -> {
                                        updateSlot(gardens, currentGardenIndex, slot.id) { it.copy(isVisible = false) }
                                            .also { gardens = it }
                                    }
                                }
                            }
                        )
                    } else {
                        GhostSlotCard {
                            updateSlot(gardens, currentGardenIndex, slot.id) { it.copy(isVisible = true) }
                                .also { gardens = it }
                        }
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // PAGINACIÓN
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { if (currentGardenIndex > 0) currentGardenIndex-- }, enabled = currentGardenIndex > 0) { Icon(Icons.Filled.ChevronLeft, null) }
                Text("${currentGardenIndex + 1} / ${gardens.size}", fontWeight = FontWeight.Bold)
                IconButton(onClick = { if (currentGardenIndex < gardens.size - 1) currentGardenIndex++ }, enabled = currentGardenIndex < gardens.size - 1) { Icon(Icons.Filled.ChevronRight, null) }
            }
        }

        // --- DIÁLOGO DE RENOMBRAR JARDINERA ---
        if (showRenameDialog) {
            AlertDialog(
                onDismissRequest = { showRenameDialog = false },
                title = { Text(stringResource(Res.string.garden_rename_title)) },
                text = {
                    OutlinedTextField(
                        value = tempGardenName,
                        onValueChange = { tempGardenName = it },
                        label = { Text(stringResource(Res.string.garden_rename_label)) },
                        singleLine = true
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (tempGardenName.isNotBlank()) {
                                updateGarden(gardens, currentGardenIndex) { it.copy(name = tempGardenName) }
                                    .also { gardens = it }
                            }
                            showRenameDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) {
                        Text(stringResource(Res.string.garden_rename_save))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRenameDialog = false }) {
                        Text(stringResource(Res.string.garden_popup_cancel))
                    }
                }
            )
        }

        // POPUP PLANTA
        if (showPlantSelector) {
            AlertDialog(
                onDismissRequest = { showPlantSelector = false },
                icon = { Icon(Icons.Filled.Grass, null) },
                title = { Text(stringResource(Res.string.garden_popup_title)) },
                text = {
                    Column {
                        ListItem(headlineContent = { Text("Tomates") }, leadingContent = { Icon(Icons.Filled.Eco, null) }, modifier = Modifier.clickable {
                            updateSlot(gardens, currentGardenIndex, selectedSlotIdForPlanting) { it.copy(contentName = "Tomates", status = "Sano", icon = Icons.Filled.Eco) }.also { gardens = it }
                            showPlantSelector = false
                        })
                        ListItem(headlineContent = { Text("Lechugas") }, leadingContent = { Icon(Icons.Filled.Eco, null) }, modifier = Modifier.clickable { showPlantSelector = false })
                    }
                },
                confirmButton = { TextButton(onClick = { showPlantSelector = false }) { Text(stringResource(Res.string.garden_popup_cancel)) } }
            )
        }
    }
}

// --- UTILIDADES ---
enum class SlotAction { HARVEST, HIDE }

fun updateGarden(list: List<GardenPage>, index: Int, update: (GardenPage) -> GardenPage): List<GardenPage> {
    val newList = list.toMutableList()
    newList[index] = update(newList[index])
    return newList
}

fun updateSlot(list: List<GardenPage>, gardenIndex: Int, slotId: String, update: (GardenSlot) -> GardenSlot): List<GardenPage> {
    val newList = list.toMutableList()
    val currentGarden = newList[gardenIndex]
    val newSlots = currentGarden.slots.map { if (it.id == slotId) update(it) else it }
    newList[gardenIndex] = currentGarden.copy(slots = newSlots)
    return newList
}

// --- TARJETAS ---
@Composable
fun GardenSlotCard(slot: GardenSlot, onClick: () -> Unit, onAction: (SlotAction) -> Unit) {
    val isEmpty = slot.contentName == null
    val bgColor = if (isEmpty) MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f) else MaterialTheme.colorScheme.surface
    var showMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            Text(slot.positionName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), modifier = Modifier.align(Alignment.TopStart))
            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Filled.MoreVert, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }, modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    if (!isEmpty) {
                        DropdownMenuItem(text = { Text(stringResource(Res.string.garden_menu_harvest)) }, leadingIcon = { Icon(Icons.Filled.Agriculture, null, tint = GreenPrimary) }, onClick = { showMenu = false; onAction(SlotAction.HARVEST) })
                        DropdownMenuItem(text = { Text(stringResource(Res.string.garden_menu_delete_content)) }, leadingIcon = { Icon(Icons.Filled.Delete, null, tint = RedDanger) }, onClick = { showMenu = false; onAction(SlotAction.HARVEST) })
                    }
                    DropdownMenuItem(text = { Text(stringResource(Res.string.garden_menu_hide_slot), fontSize = 12.sp) }, leadingIcon = { Icon(Icons.Filled.VisibilityOff, null, tint = MaterialTheme.colorScheme.secondary) }, onClick = { showMenu = false; onAction(SlotAction.HIDE) })
                }
            }
            Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                if (isEmpty) {
                    Icon(Icons.Filled.AddCircleOutline, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), modifier = Modifier.size(24.dp))
                    Text(stringResource(Res.string.garden_empty), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                } else {
                    Icon(slot.icon ?: Icons.Filled.Eco, null, tint = GreenPrimary, modifier = Modifier.size(32.dp))
                    Text(slot.contentName ?: "", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                    if (slot.status != null) {
                        Spacer(Modifier.height(2.dp))
                        StatusPill(status = slot.status)
                    }
                }
            }
        }
    }
}

@Composable
fun GhostSlotCard(onRestore: () -> Unit) {
    val stroke = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
    val color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f).padding(4.dp).clip(RoundedCornerShape(12.dp)).clickable { onRestore() }.drawBehind { drawRoundRect(color = color, style = stroke, cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx())) }, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Add, null, tint = color)
            Text(stringResource(Res.string.garden_restore_slot), fontSize = 8.sp, color = color, lineHeight = 10.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}