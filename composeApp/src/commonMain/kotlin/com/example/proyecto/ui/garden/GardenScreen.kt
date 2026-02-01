package com.example.proyecto.ui.garden

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.data.database.entity.BancalEntity
import com.example.proyecto.data.database.entity.JardineraEntity
import com.example.proyecto.ui.StatusPill
import com.example.proyecto.ui.navigation.AppScreens
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.RedDanger
import org.jetbrains.compose.resources.stringResource
import huertomanager.composeapp.generated.resources.*
import org.koin.compose.viewmodel.koinViewModel

// 1. Modelo de datos para la UI
data class GardenSlot(
    val id: Long,
    val positionName: String,
    val contentName: String?,
    val status: String?,
    val icon: ImageVector?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenScreen(
    navController: NavController,
    initialGardenIndex: Int = 0,
    viewModel: GardenViewModel = koinViewModel()
) {
    val jardineras by viewModel.jardineras.collectAsState()
    var currentGardenIndex by remember { mutableStateOf(initialGardenIndex) }
    val currentJardinera = jardineras.getOrNull(currentGardenIndex)

    val bancales by if (currentJardinera != null) {
        viewModel.getBancales(currentJardinera.id).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList<BancalEntity>()) }
    }

    var showPlantSelector by remember { mutableStateOf(false) }
    var selectedSlotIdForPlanting by remember { mutableStateOf<Long?>(null) }
    var showAddGardenDialog by remember { mutableStateOf(false) }
    var showGardenMenu by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf("") }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
            if (currentJardinera == null) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Button(onClick = { showAddGardenDialog = true }) { Text("Crear mi primera jardinera") }
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = currentJardinera.nombre, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text("${currentJardinera.filas}x${currentJardinera.columnas} - ${stringResource(Res.string.garden_columns)}", fontSize = 12.sp)
                    }

                    Box {
                        IconButton(onClick = { showGardenMenu = true }) { Icon(Icons.Filled.MoreVert, null) }
                        DropdownMenu(expanded = showGardenMenu, onDismissRequest = { showGardenMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Eliminar", color = RedDanger) },
                                leadingIcon = { Icon(Icons.Default.Delete, null, tint = RedDanger) },
                                onClick = { showGardenMenu = false }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(currentJardinera.columnas),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(bancales) { bancal ->
                        GardenSlotCard(
                            slot = GardenSlot(
                                id = bancal.id,
                                positionName = "${bancal.fila + 1}-${bancal.columna + 1}",
                                contentName = bancal.nombreCultivo,
                                status = if (bancal.cultivoSlug != null) "Sano" else null,
                                icon = if (bancal.cultivoSlug != null) Icons.Filled.Eco else null
                            ),
                            onClick = {
                                if (bancal.nombreCultivo == null) {
                                    selectedSlotIdForPlanting = bancal.id
                                    showPlantSelector = true
                                } else {
                                    navController.navigate(AppScreens.createSlotDetailRoute(bancal.id.toString()))
                                }
                            }
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
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
            confirmButton = { Button(onClick = { if(tempName.isNotBlank()) viewModel.crearNuevaJardinera(tempName); showAddGardenDialog = false; tempName = "" }) { Text("Crear") } }
        )
    }

    if (showPlantSelector) {
        AlertDialog(
            onDismissRequest = { showPlantSelector = false },
            title = { Text(stringResource(Res.string.garden_popup_title)) },
            text = {
                Column {
                    ListItem(
                        headlineContent = { Text("Tomate Cherry") },
                        leadingContent = { Icon(Icons.Filled.Eco, null, tint = GreenPrimary) },
                        modifier = Modifier.clickable {
                            selectedSlotIdForPlanting?.let { viewModel.plantar(it, "tomato") }
                            showPlantSelector = false
                        }
                    )
                }
            },
            confirmButton = { TextButton(onClick = { showPlantSelector = false }) { Text("Cerrar") } }
        )
    }
}

// COMPONENTE QUE FALTABA
@Composable
fun GardenSlotCard(slot: GardenSlot, onClick: () -> Unit) {
    val isEmpty = slot.contentName == null
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if(isEmpty) MaterialTheme.colorScheme.surfaceVariant.copy(0.4f) else MaterialTheme.colorScheme.surface),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            if (isEmpty) {
                Icon(Icons.Filled.AddCircleOutline, null, tint = MaterialTheme.colorScheme.onSurface.copy(0.3f), modifier = Modifier.size(28.dp))
                Text(stringResource(Res.string.garden_empty), fontSize = 10.sp)
            } else {
                Icon(Icons.Filled.Eco, null, tint = GreenPrimary, modifier = Modifier.size(32.dp))
                Text(text = slot.contentName ?: "", fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                slot.status?.let { StatusPill(it) }
            }
        }
    }
}