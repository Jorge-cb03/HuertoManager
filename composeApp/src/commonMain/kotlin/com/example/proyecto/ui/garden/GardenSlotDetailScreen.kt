package com.example.proyecto.ui.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.data.database.entity.BancalEntity
import com.example.proyecto.data.database.entity.EntradaDiarioEntity
import com.example.proyecto.ui.HuertaCard
import com.example.proyecto.ui.theme.GreenPrimary
import org.jetbrains.compose.resources.stringResource
import huertomanager.composeapp.generated.resources.*
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenSlotDetailScreen(
    navController: NavController,
    bancalId: String, // Recibimos el ID real de la base de datos
    viewModel: GardenViewModel = koinViewModel()
) {
    val id = bancalId.toLongOrNull() ?: 0L
    var showInfoPopup by remember { mutableStateOf(false) }
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }

    // --- OBSERVACIÓN DE DATOS REALES ---
    var bancal by remember { mutableStateOf<BancalEntity?>(null) }
    val historial by viewModel.getHistorial(id).collectAsState(initial = emptyList())

    LaunchedEffect(id) {
        bancal = viewModel.getBancalById(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(bancal?.nombreCultivo ?: stringResource(Res.string.garden_empty)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // --- BOX PARA LA IMAGEN DE LA API (RESTAURADO) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                // Aquí irá Coil más adelante. De momento dejamos el diseño intacto.
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Image,
                        null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        "Imagen de OpenFarm",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                // --- CABECERA: NOMBRE E ICONO INFO (DATOS REALES) ---
                HuertaCard {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = bancal?.nombreCultivo ?: stringResource(Res.string.garden_empty),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (bancal?.nombreCultivo != null) {
                                    IconButton(onClick = { showInfoPopup = true }) {
                                        Icon(Icons.Default.Info, "Info", tint = GreenPrimary, modifier = Modifier.size(24.dp))
                                    }
                                }
                            }
                            Text(
                                text = "Bancal: Fila ${bancal?.fila?.plus(1)} - Col ${bancal?.columna?.plus(1)}",
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                Spacer(Modifier.height(25.dp))

                // --- ACCIONES RÁPIDAS (DISEÑO ORIGINAL) ---
                Text(
                    text = stringResource(Res.string.quick_actions_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val actions = listOf(
                        Triple(stringResource(Res.string.action_water), Icons.Default.WaterDrop, "Riego"),
                        Triple(stringResource(Res.string.action_prune), Icons.Default.ContentCut, "Poda"),
                        Triple(stringResource(Res.string.action_pest), Icons.Default.BugReport, "Antiplaga"),
                        Triple(stringResource(Res.string.action_fertilize), Icons.Default.Science, "Abonado")
                    )

                    actions.forEach { (label, icon, taskTitle) ->
                        QuickActionItem(label, icon, Modifier.weight(1f)) {
                            // Aquí lanzaremos la acción real a la base de datos en el futuro
                        }
                    }
                }

                Spacer(Modifier.height(25.dp))

                // --- HISTORIAL SINCRONIZADO (TIMELINE ORIGINAL) ---
                Text(
                    text = stringResource(Res.string.history_care_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 15.dp)
                )

                if (historial.isEmpty()) {
                    Text("No hay registros en el diario.", color = Color.Gray, fontSize = 14.sp)
                } else {
                    historial.sortedByDescending { it.fecha }.forEachIndexed { index, entrada ->
                        val date = Instant.fromEpochMilliseconds(entrada.fecha).toLocalDateTime(TimeZone.currentSystemDefault())
                        val timeStr = if (date.date == today) "Hoy" else "${date.dayOfMonth}/${date.monthNumber}"

                        TimelineItem(
                            title = entrada.tipoAccion,
                            desc = entrada.descripcion, // CORREGIDO: descripcion
                            time = timeStr,
                            icon = when (entrada.tipoAccion) {
                                "SIEMBRA" -> Icons.Default.Eco
                                "Riego" -> Icons.Default.WaterDrop
                                "Poda" -> Icons.Default.ContentCut
                                "Abonado" -> Icons.Default.Science
                                else -> Icons.Default.Agriculture
                            },
                            color = if (entrada.tipoAccion == "SIEMBRA") GreenPrimary else MaterialTheme.colorScheme.primary,
                            showLine = index != historial.size - 1
                        )
                    }
                }

                Spacer(Modifier.height(30.dp))
            }
        }
    }

    // POPUP INFO TÉCNICA (RESTURADO)
    if (showInfoPopup) {
        AlertDialog(
            onDismissRequest = { showInfoPopup = false },
            title = { Text(stringResource(Res.string.plant_info_title)) },
            text = {
                Column {
                    InfoRow(stringResource(Res.string.plant_info_fruit), "Información cargada desde OpenFarm.")
                    InfoRow(stringResource(Res.string.plant_info_companions), "Albahaca, Caléndula, Zanahoria.")
                    InfoRow(stringResource(Res.string.plant_info_antagonists), "Patatas, Hinojo.")
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoPopup = false }) {
                    Text(stringResource(Res.string.dialog_btn_ok))
                }
            }
        )
    }
}

// --- COMPONENTES DE APOYO (RESTAURADOS) ---

@Composable
fun QuickActionItem(label: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = GreenPrimary, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(6.dp))
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}

@Composable
fun TimelineItem(title: String, desc: String, time: String, icon: ImageVector, color: Color, showLine: Boolean) {
    Row(Modifier.height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(42.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .border(2.dp, color, CircleShape)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
            }
            if (showLine) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        HuertaCard(modifier = Modifier.padding(bottom = 16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(time, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
            }
            Text(text = desc, color = MaterialTheme.colorScheme.secondary, fontSize = 13.sp)
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(Modifier.padding(vertical = 6.dp)) {
        Text(label, fontWeight = FontWeight.Bold, color = GreenPrimary, fontSize = 14.sp)
        Text(value, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}