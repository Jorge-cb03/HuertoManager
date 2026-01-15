package com.example.proyecto.ui.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.ui.StatusPill
import com.example.proyecto.ui.navigation.AppScreens
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

// Modelos de datos
data class GardenSlot(
    val id: String,
    val positionName: String,
    val contentName: String?,
    val status: String?,
    val icon: ImageVector?
)

data class GardenPage(
    val id: String,
    val name: String,
    val slots: List<GardenSlot>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenScreen(navController: NavController) {
    var currentGardenIndex by remember { mutableStateOf(0) }
    var showPlantSelector by remember { mutableStateOf(false) }

    // Datos simulados
    var gardens by remember {
        mutableStateOf(
            listOf(
                GardenPage("1", "Invernadero", listOf(
                    GardenSlot("s1", "1", "Tomates", "Sano", Icons.Filled.Eco),
                    GardenSlot("s2", "2", "Lechugas", "Enfermo", Icons.Filled.Grass),
                    GardenSlot("s3", "3", null, null, null),
                    GardenSlot("s4", "4", null, null, null),
                    GardenSlot("s5", "5", "Zanahorias", "Sano", Icons.Filled.Eco),
                    GardenSlot("s6", "6", null, null, null),
                    GardenSlot("s7", "7", null, null, null),
                    GardenSlot("s8", "8", null, null, null)
                )),
                GardenPage("2", "Terraza", List(8) { idx -> GardenSlot("t$idx", "${idx+1}", null, null, null) }),
                GardenPage("3", "Cama Alta", List(8) { idx -> if(idx == 0) GardenSlot("c$idx", "1", "Flores", "Sano", Icons.Filled.LocalFlorist) else GardenSlot("c$idx", "${idx+1}", null, null, null) })
            )
        )
    }

    val currentGarden = gardens[currentGardenIndex]

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // CABECERA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentGarden.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    onClick = { /* Lógica añadir jardinera */ },
                    shape = CircleShape,
                    color = Color(0xFF8B5CF6),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Add, null, tint = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // GRID
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(currentGarden.slots) { slot ->
                    GardenSlotCard(
                        slot = slot,
                        onClick = {
                            if (slot.contentName == null) {
                                showPlantSelector = true
                            } else {
                                navController.navigate(AppScreens.createSlotDetailRoute(slot.positionName))
                            }
                        },
                        onClearSlot = {
                            val newGardens = gardens.map { garden ->
                                if (garden.id == currentGarden.id) {
                                    val newSlots = garden.slots.map { s ->
                                        if (s.id == slot.id) s.copy(contentName = null, status = null, icon = null) else s
                                    }
                                    garden.copy(slots = newSlots)
                                } else garden
                            }
                            gardens = newGardens
                        }
                    )
                }
            }

            // NAVEGACIÓN INFERIOR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (currentGardenIndex > 0) currentGardenIndex-- },
                    enabled = currentGardenIndex > 0
                ) {
                    Icon(Icons.Filled.ChevronLeft, null, tint = MaterialTheme.colorScheme.onSurface)
                }
                Text("${currentGardenIndex + 1} / ${gardens.size}", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                IconButton(
                    onClick = { if (currentGardenIndex < gardens.size - 1) currentGardenIndex++ },
                    enabled = currentGardenIndex < gardens.size - 1
                ) {
                    Icon(Icons.Filled.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        // POPUP PLANTAR TRADUCIDO
        if (showPlantSelector) {
            AlertDialog(
                onDismissRequest = { showPlantSelector = false },
                icon = { Icon(Icons.Filled.Grass, null) },
                title = { Text(stringResource(Res.string.garden_popup_title)) },
                text = {
                    Column {
                        ListItem(headlineContent = { Text("Tomates") }, leadingContent = { Icon(Icons.Filled.Eco, null) }, modifier = Modifier.clickable { showPlantSelector = false })
                        ListItem(headlineContent = { Text("Lechugas") }, leadingContent = { Icon(Icons.Filled.Eco, null) }, modifier = Modifier.clickable { showPlantSelector = false })
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showPlantSelector = false }) {
                        Text(stringResource(Res.string.garden_popup_cancel))
                    }
                }
            )
        }
    }
}

@Composable
fun GardenSlotCard(slot: GardenSlot, onClick: () -> Unit, onClearSlot: () -> Unit) {
    val isEmpty = slot.contentName == null
    val bgColor = if (isEmpty) MaterialTheme.colorScheme.surface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
    var showMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Text(slot.positionName, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.TopStart))

            // MENÚ BORRAR TRADUCIDO
            if (!isEmpty) {
                Box(modifier = Modifier.align(Alignment.TopEnd)) {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Filled.MoreVert, "Opciones", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.garden_menu_harvest)) },
                            leadingIcon = { Icon(Icons.Filled.Agriculture, null, tint = Color(0xFF4DB6AC)) },
                            onClick = { showMenu = false; onClearSlot() }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.garden_menu_remove)) },
                            leadingIcon = { Icon(Icons.Filled.Delete, null, tint = Color(0xFFE57373)) },
                            onClick = { showMenu = false; onClearSlot() }
                        )
                    }
                }
            }

            // CONTENIDO
            Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                if (isEmpty) {
                    Icon(Icons.Filled.AddCircleOutline, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), modifier = Modifier.size(32.dp))
                    Text(stringResource(Res.string.garden_empty), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                } else {
                    Icon(slot.icon ?: Icons.Filled.Eco, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.height(8.dp))
                    Text(slot.contentName ?: "", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    if (slot.status != null) {
                        Spacer(Modifier.height(4.dp))
                        StatusPill(status = slot.status)
                    }
                }
            }
        }
    }
}