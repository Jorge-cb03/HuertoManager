package com.example.proyecto.ui.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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

    // --- ESTADOS PARA DIÁLOGOS Y ALERTAS ---
    var showRenameDialog by remember { mutableStateOf(false) }
    var showAddGardenDialog by remember { mutableStateOf(false) } // Nuevo: Añadir Jardinera
    var showDeleteGardenDialog by remember { mutableStateOf(false) } // Nuevo: Borrar Jardinera
    var showSuccessDialog by remember { mutableStateOf<String?>(null) } // Nuevo: Alerta Éxito genérica

    var tempName by remember { mutableStateOf("") }

    // Estados para menú superior
    var showGardenMenu by remember { mutableStateOf(false) }

    // Estados para confirmación de acciones en huecos
    var actionToConfirm by remember { mutableStateOf<SlotAction?>(null) }
    var slotIdToConfirm by remember { mutableStateOf<String?>(null) }

    // --- DATOS INICIALES ---
    var gardens by remember {
        mutableStateOf(
            listOf(
                GardenPage("1", "Invernadero", 2, List(8) { idx -> GardenSlot("s$idx", "${idx+1}", if(idx==0) "Tomates" else null, if(idx==0) "Sano" else null, if(idx==0) Icons.Filled.Eco else null) }),
                GardenPage("2", "Terraza", 2, List(8) { idx -> GardenSlot("t$idx", "${idx+1}", null, null, null) })
            )
        )
    }

    // Asegurar índice válido tras borrar
    if (currentGardenIndex >= gardens.size && gardens.isNotEmpty()) {
        currentGardenIndex = gardens.size - 1
    }
    val currentGarden = if (gardens.isNotEmpty()) gardens[currentGardenIndex] else null

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentGarden == null) {
                // Caso borde: No hay jardineras
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Button(onClick = { showAddGardenDialog = true }) { Text("Crear mi primera jardinera") }
                }
            } else {
                // CABECERA CON MENÚ DE 3 PUNTOS (NUEVO)
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
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.pointerInput(Unit) {
                                detectTapGestures(onDoubleTap = { tempName = currentGarden.name; showRenameDialog = true })
                            }
                        )
                        Text("${currentGarden.columns} ${stringResource(Res.string.garden_columns)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                    }

                    // MENÚ DESPLEGABLE (Sustituye al botón +)
                    Box {
                        IconButton(onClick = { showGardenMenu = true }) {
                            Icon(Icons.Filled.MoreVert, null)
                        }
                        DropdownMenu(
                            expanded = showGardenMenu,
                            onDismissRequest = { showGardenMenu = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.menu_add_garden)) },
                                leadingIcon = { Icon(Icons.Filled.Add, null) },
                                onClick = { showGardenMenu = false; tempName = ""; showAddGardenDialog = true }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.menu_delete_garden), color = RedDanger) },
                                leadingIcon = { Icon(Icons.Filled.Delete, null, tint = RedDanger) },
                                onClick = { showGardenMenu = false; showDeleteGardenDialog = true }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // CONTROLES DE FILAS/COLUMNAS (Igual que antes)
                Row(
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp)).padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (currentGarden.columns > 1) updateGarden(gardens, currentGardenIndex) { it.copy(columns = it.columns - 1) }.also { gardens = it } }, modifier = Modifier.size(32.dp)) { Icon(Icons.Filled.Remove, null) }
                        Text("${currentGarden.columns} Col", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp), fontSize = 14.sp)
                        IconButton(onClick = {
                            val currentRows = (currentGarden.slots.size + currentGarden.columns - 1) / currentGarden.columns
                            val totalSlotsNeeded = currentRows * (currentGarden.columns + 1)
                            val slotsToAddCount = totalSlotsNeeded - currentGarden.slots.size
                            val newSlots = List(slotsToAddCount) { GardenSlot("${currentGarden.id}_extra_${System.currentTimeMillis()}_$it", "${currentGarden.slots.size + it + 1}", null, null, null, true) }
                            updateGarden(gardens, currentGardenIndex) { it.copy(columns = it.columns + 1, slots = it.slots + newSlots) }.also { gardens = it }
                        }, modifier = Modifier.size(32.dp)) { Icon(Icons.Filled.Add, null) }
                    }
                    Button(
                        onClick = {
                            val newSlots = List(currentGarden.columns) { GardenSlot("${currentGarden.id}_${System.currentTimeMillis()}_$it", "${currentGarden.slots.size + it + 1}", null, null, null, true) }
                            updateGarden(gardens, currentGardenIndex) { it.copy(slots = it.slots + newSlots) }.also { gardens = it }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp), modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Filled.TableRows, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(Res.string.garden_add_row), fontSize = 12.sp)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // GRID
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
                                onAction = { action -> slotIdToConfirm = slot.id; actionToConfirm = action }
                            )
                        } else {
                            GhostSlotCard { updateSlot(gardens, currentGardenIndex, slot.id) { it.copy(isVisible = true) }.also { gardens = it } }
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
                // PAGINACIÓN
                Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 5.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (currentGardenIndex > 0) currentGardenIndex-- }, enabled = currentGardenIndex > 0) { Icon(Icons.Filled.ChevronLeft, null) }
                    Text("${currentGardenIndex + 1} / ${gardens.size}", fontWeight = FontWeight.Bold)
                    IconButton(onClick = { if (currentGardenIndex < gardens.size - 1) currentGardenIndex++ }, enabled = currentGardenIndex < gardens.size - 1) { Icon(Icons.Filled.ChevronRight, null) }
                }
            }
        }

        // --- DIÁLOGOS DE CREACIÓN / EDICIÓN / BORRADO ---

        // 1. AÑADIR JARDINERA
        if (showAddGardenDialog) {
            AlertDialog(
                onDismissRequest = { showAddGardenDialog = false },
                title = { Text(stringResource(Res.string.dialog_garden_add_title)) },
                text = { OutlinedTextField(value = tempName, onValueChange = { tempName = it }, label = { Text(stringResource(Res.string.dialog_garden_add_hint)) }, singleLine = true) },
                confirmButton = {
                    Button(onClick = {
                        val newId = (gardens.size + 1).toString()
                        val finalName = if (tempName.isBlank()) "Jardinera $newId" else tempName
                        val newPage = GardenPage(newId, finalName, 2, List(8) { idx -> GardenSlot("n${newId}_$idx", "${idx+1}", null, null, null) })
                        gardens = gardens + newPage
                        currentGardenIndex = gardens.size - 1
                        showAddGardenDialog = false
                        showSuccessDialog = "Jardinera creada correctamente" // Disparar alerta éxito
                    }, colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) { Text(stringResource(Res.string.dialog_confirm)) }
                },
                dismissButton = { TextButton(onClick = { showAddGardenDialog = false }) { Text(stringResource(Res.string.dialog_cancel)) } }
            )
        }

        // 2. BORRAR JARDINERA COMPLETA
        if (showDeleteGardenDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteGardenDialog = false },
                title = { Text(stringResource(Res.string.dialog_garden_delete_title)) },
                text = { Text(stringResource(Res.string.dialog_garden_delete_msg)) },
                confirmButton = {
                    Button(onClick = {
                        val newGardens = gardens.toMutableList().apply { removeAt(currentGardenIndex) }
                        gardens = newGardens
                        showDeleteGardenDialog = false
                        showSuccessDialog = "Jardinera eliminada" // Disparar alerta éxito
                    }, colors = ButtonDefaults.buttonColors(containerColor = RedDanger)) { Text(stringResource(Res.string.dialog_confirm)) }
                },
                dismissButton = { TextButton(onClick = { showDeleteGardenDialog = false }) { Text(stringResource(Res.string.dialog_cancel)) } }
            )
        }

        // 3. RENOMBRAR (DOBLE CLIC)
        if (showRenameDialog) {
            AlertDialog(
                onDismissRequest = { showRenameDialog = false },
                title = { Text(stringResource(Res.string.garden_rename_title)) },
                text = { OutlinedTextField(value = tempName, onValueChange = { tempName = it }, label = { Text(stringResource(Res.string.garden_rename_label)) }, singleLine = true) },
                confirmButton = {
                    Button(onClick = {
                        if (tempName.isNotBlank()) {
                            updateGarden(gardens, currentGardenIndex) { it.copy(name = tempName) }.also { gardens = it }
                            showSuccessDialog = "Nombre actualizado"
                        }
                        showRenameDialog = false
                    }, colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) { Text(stringResource(Res.string.garden_rename_save)) }
                },
                dismissButton = { TextButton(onClick = { showRenameDialog = false }) { Text(stringResource(Res.string.dialog_cancel)) } }
            )
        }

        // 4. ALERTA DE ÉXITO GENÉRICA
        if (showSuccessDialog != null) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = null },
                title = { Text(stringResource(Res.string.dialog_success_title)) },
                text = { Text(showSuccessDialog!!) },
                confirmButton = { Button(onClick = { showSuccessDialog = null }, colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) { Text(stringResource(Res.string.dialog_btn_ok)) } }
            )
        }

        // 5. POPUP PLANTA (Existente)
        if (showPlantSelector) {
            AlertDialog(
                onDismissRequest = { showPlantSelector = false },
                icon = { Icon(Icons.Filled.Grass, null) },
                title = { Text(stringResource(Res.string.garden_popup_title)) },
                text = {
                    Column {
                        ListItem(headlineContent = { Text("Tomates") }, leadingContent = { Icon(Icons.Filled.Eco, null) }, modifier = Modifier.clickable { updateSlot(gardens, currentGardenIndex, selectedSlotIdForPlanting) { it.copy(contentName = "Tomates", status = "Sano", icon = Icons.Filled.Eco) }.also { gardens = it }; showPlantSelector = false })
                        ListItem(headlineContent = { Text("Lechugas") }, leadingContent = { Icon(Icons.Filled.Eco, null) }, modifier = Modifier.clickable { showPlantSelector = false })
                    }
                },
                confirmButton = { TextButton(onClick = { showPlantSelector = false }) { Text(stringResource(Res.string.garden_popup_cancel)) } }
            )
        }

        // 6. CONFIRMACIÓN HUECOS (Existente)
        if (actionToConfirm != null && slotIdToConfirm != null) {
            val title = if (actionToConfirm == SlotAction.HARVEST) stringResource(Res.string.dialog_harvest_title) else stringResource(Res.string.dialog_delete_slot_title)
            val msg = if (actionToConfirm == SlotAction.HARVEST) stringResource(Res.string.dialog_harvest_msg) else stringResource(Res.string.dialog_delete_slot_msg)

            AlertDialog(
                onDismissRequest = { actionToConfirm = null },
                title = { Text(title) }, text = { Text(msg) },
                confirmButton = {
                    Button(onClick = {
                        if (actionToConfirm == SlotAction.HARVEST) { updateSlot(gardens, currentGardenIndex, slotIdToConfirm!!) { it.copy(contentName = null, status = null, icon = null) }.also { gardens = it }
                        } else { updateSlot(gardens, currentGardenIndex, slotIdToConfirm!!) { it.copy(isVisible = false) }.also { gardens = it } }
                        actionToConfirm = null; slotIdToConfirm = null
                    }, colors = ButtonDefaults.buttonColors(containerColor = RedDanger)) { Text(stringResource(Res.string.dialog_confirm)) }
                },
                dismissButton = { TextButton(onClick = { actionToConfirm = null }) { Text(stringResource(Res.string.dialog_cancel)) } }
            )
        }
    }
}

// UTILIDADES
enum class SlotAction { HARVEST, HIDE }
fun updateGarden(list: List<GardenPage>, index: Int, update: (GardenPage) -> GardenPage): List<GardenPage> {
    val newList = list.toMutableList(); newList[index] = update(newList[index]); return newList
}
fun updateSlot(list: List<GardenPage>, gardenIndex: Int, slotId: String, update: (GardenSlot) -> GardenSlot): List<GardenPage> {
    val newList = list.toMutableList(); val cur = newList[gardenIndex]
    val newSlots = cur.slots.map { if (it.id == slotId) update(it) else it }
    newList[gardenIndex] = cur.copy(slots = newSlots); return newList
}
@Composable
fun GardenSlotCard(slot: GardenSlot, onClick: () -> Unit, onAction: (SlotAction) -> Unit) {
    val isEmpty = slot.contentName == null
    var showMenu by remember { mutableStateOf(false) }
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = if(isEmpty) MaterialTheme.colorScheme.surfaceVariant.copy(0.5f) else MaterialTheme.colorScheme.surface), onClick = onClick, modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
        Box(Modifier.fillMaxSize().padding(8.dp)) {
            Text(slot.positionName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.4f), modifier = Modifier.align(Alignment.TopStart))
            Box(Modifier.align(Alignment.TopEnd)) {
                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) { Icon(Icons.Filled.MoreVert, null, tint = MaterialTheme.colorScheme.onSurface.copy(0.5f)) }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }, modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    if (!isEmpty) {
                        DropdownMenuItem(text = { Text(stringResource(Res.string.garden_menu_harvest)) }, leadingIcon = { Icon(Icons.Filled.Agriculture, null, tint = GreenPrimary) }, onClick = { showMenu = false; onAction(SlotAction.HARVEST) })
                        DropdownMenuItem(text = { Text(stringResource(Res.string.garden_menu_delete_content)) }, leadingIcon = { Icon(Icons.Filled.Delete, null, tint = RedDanger) }, onClick = { showMenu = false; onAction(SlotAction.HARVEST) })
                    }
                    DropdownMenuItem(text = { Text(stringResource(Res.string.garden_menu_hide_slot), fontSize = 12.sp) }, leadingIcon = { Icon(Icons.Filled.VisibilityOff, null, tint = MaterialTheme.colorScheme.secondary) }, onClick = { showMenu = false; onAction(SlotAction.HIDE) })
                }
            }
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                if (isEmpty) { Icon(Icons.Filled.AddCircleOutline, null, tint = MaterialTheme.colorScheme.onSurface.copy(0.2f), modifier = Modifier.size(24.dp)); Text(stringResource(Res.string.garden_empty), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(0.5f)) }
                else { Icon(slot.icon ?: Icons.Filled.Eco, null, tint = GreenPrimary, modifier = Modifier.size(32.dp)); Text(slot.contentName ?: "", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, maxLines = 1); if (slot.status != null) { Spacer(Modifier.height(2.dp)); StatusPill(slot.status) } }
            }
        }
    }
}
@Composable
fun GhostSlotCard(onRestore: () -> Unit) {
    val stroke = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
    val color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f).padding(4.dp).clip(RoundedCornerShape(12.dp)).clickable { onRestore() }.drawBehind { drawRoundRect(color = color, style = stroke, cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx())) }, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Filled.Add, null, tint = color); Text(stringResource(Res.string.garden_restore_slot), fontSize = 8.sp, color = color, lineHeight = 10.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center) }
    }
}