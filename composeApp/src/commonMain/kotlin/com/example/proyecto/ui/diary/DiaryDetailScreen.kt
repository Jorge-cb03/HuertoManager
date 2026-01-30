package com.example.proyecto.ui.diary

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.data.database.entity.EntradaDiarioEntity
import com.example.proyecto.ui.garden.GardenViewModel
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.RedDanger
import kotlinx.datetime.*
import org.koin.compose.viewmodel.koinViewModel
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryDetailScreen(navController: NavController, taskId: Long, viewModel: GardenViewModel = koinViewModel()) {
    var entrada by remember { mutableStateOf<EntradaDiarioEntity?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(taskId) {
        entrada = viewModel.getEntradaDiarioById(taskId)
    }

    Scaffold(
        // TopBar transparente para que se fusione con el header
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape)
                    ) { Icon(Icons.Rounded.ArrowBack, null) }
                },
                actions = {
                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), CircleShape)
                    ) { Icon(Icons.Rounded.Delete, null, tint = RedDanger) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        // Botón de acción flotante extendido para Editar
        floatingActionButton = {
            entrada?.let { item ->
                ExtendedFloatingActionButton(
                    onClick = {
                        navController.navigate("add_diary_entry/${item.fecha}?taskId=${item.id}&title=${item.tipoAccion}&desc=${item.descripcion}")
                    },
                    containerColor = GreenPrimary,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Rounded.Edit, null) },
                    text = { Text("Editar Tarea", fontWeight = FontWeight.Bold) }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        entrada?.let { item ->
            val date = Instant.fromEpochMilliseconds(item.fecha).toLocalDateTime(TimeZone.currentSystemDefault())
            val formattedDate = "${date.dayOfMonth} de ${date.month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))} de ${date.year}"

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // --- HERO HEADER VISUAL ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        // Fondo verde suave con forma redondeada abajo
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(GreenPrimary.copy(alpha = 0.15f))
                        .padding(top = padding.calculateTopPadding() + 20.dp, bottom = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Icono Grande y Temático
                        Icon(
                            imageVector = getIconForAction(item.tipoAccion),
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier
                                .size(80.dp)
                                .background(Color.White, CircleShape)
                                .padding(16.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        // Título Grande
                        Text(
                            text = item.tipoAccion.uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = GreenPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        // Fecha con estilo
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.CalendarToday, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                // --- CONTENIDO PRINCIPAL ---
                Column(modifier = Modifier.padding(24.dp)) {
                    // Tarjeta de Descripción Moderna
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Description, null, tint = GreenPrimary)
                                Spacer(Modifier.width(12.dp))
                                Text("Detalles de la actividad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                            if (item.descripcion.isNotBlank()) {
                                Text(
                                    text = item.descripcion,
                                    style = MaterialTheme.typography.bodyLarge,
                                    lineHeight = 24.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                )
                            } else {
                                Text(
                                    text = "Sin descripción añadida.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                    }
                    // Espacio extra al final para que el FAB no tape contenido
                    Spacer(Modifier.height(80.dp))
                }
            }
        }
    }

    // Dialogo de eliminación (igual que antes, funcional)
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            icon = { Icon(Icons.Rounded.DeleteForever, null, tint = RedDanger) },
            title = { Text("¿Eliminar tarea permanentemente?") },
            text = { Text("Esta acción no se puede deshacer y la información se perderá.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.eliminarEntradaDiario(taskId); navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDanger)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Helper para elegir el icono según el texto de la acción
@Composable
fun getIconForAction(actionType: String): ImageVector {
    return when (actionType.uppercase().trim()) {
        "SIEMBRA" -> Icons.Rounded.Eco
        "RIEGO" -> Icons.Rounded.WaterDrop
        "COSECHA" -> Icons.Rounded.Agriculture
        "PODA" -> Icons.Rounded.ContentCut
        "FERTILIZANTE" -> Icons.Rounded.Science
        "TRASPLANTE" -> Icons.Rounded.MoveDown
        else -> Icons.Rounded.EventNote
    }
}