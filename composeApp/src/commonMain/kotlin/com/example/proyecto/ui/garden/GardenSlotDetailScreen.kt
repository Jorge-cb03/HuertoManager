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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.ui.HuertaCard
import com.example.proyecto.ui.diary.DiaryTask
import com.example.proyecto.ui.theme.GreenPrimary
import kotlinx.datetime.*
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenSlotDetailScreen(
    navController: NavController,
    slotName: String
) {
    var showInfoPopup by remember { mutableStateOf(false) }
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }

    // Simulación de historial (Sincronizado con Diario)
    val diaryHistory = remember {
        mutableStateListOf(
            DiaryTask("1", "Riego", "08:30 AM", "Riego manual", slotName, today.minus(1, DateTimeUnit.DAY)),
            DiaryTask("2", "Abonado", "10:00 AM", "Abono orgánico", slotName, today.minus(3, DateTimeUnit.DAY))
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(slotName) },
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
            // --- NUEVO: SITIO PARA LA IMAGEN DE LA API ---
            // Aquí es donde tu compañero pondrá la llamada a la API (usando Coil, Kamel, etc.)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder temporal hasta que conectéis la API
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        "Espacio para Imagen API",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                // --- CABECERA (FOTO 1): NOMBRE E ICONO INFO ---
                HuertaCard {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Tomates Cherry", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                IconButton(onClick = { showInfoPopup = true }) {
                                    Icon(Icons.Default.Info, "Info", tint = GreenPrimary, modifier = Modifier.size(24.dp))
                                }
                            }
                            Text("Plantado en: $slotName", color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }

                Spacer(Modifier.height(25.dp))

                // --- ACCIONES RÁPIDAS (FOTO 2) ---
                Text(
                    stringResource(Res.string.quick_actions_title),
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
                            diaryHistory.add(0, DiaryTask("${Clock.System.now().toEpochMilliseconds()}", taskTitle, "Ahora", "Acción Rápida", slotName, today))
                        }
                    }
                }

                Spacer(Modifier.height(25.dp))

                // --- HISTORIAL (FOTO 3): SINCRONIZADO ---
                Text(
                    stringResource(Res.string.history_care_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 15.dp)
                )

                diaryHistory.forEachIndexed { index, task ->
                    TimelineItem(
                        title = task.title,
                        desc = task.description,
                        time = if(task.date == today) "Hoy" else "${task.date.dayOfMonth}/${task.date.monthNumber}",
                        icon = when(task.title) {
                            "Riego" -> Icons.Default.WaterDrop
                            "Poda" -> Icons.Default.ContentCut
                            "Antiplaga" -> Icons.Default.BugReport
                            "Abonado" -> Icons.Default.Science
                            else -> Icons.Default.Agriculture
                        },
                        color = GreenPrimary,
                        showLine = index != diaryHistory.size - 1
                    )
                }

                Spacer(Modifier.height(30.dp))
            }
        }
    }

    // POPUP INFO TÉCNICA
    if (showInfoPopup) {
        AlertDialog(
            onDismissRequest = { showInfoPopup = false },
            title = { Text(stringResource(Res.string.plant_info_title)) },
            text = {
                Column {
                    InfoRow(stringResource(Res.string.plant_info_fruit), "Tomate rojo pequeño y dulce.")
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
fun InfoRow(label: String, value: String) {
    Column(Modifier.padding(vertical = 6.dp)) {
        Text(label, fontWeight = FontWeight.Bold, color = GreenPrimary, fontSize = 14.sp)
        Text(value, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
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