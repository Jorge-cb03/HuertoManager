package com.example.proyecto.ui.diary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Image
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
import com.example.proyecto.data.database.entity.JardineraEntity
import com.example.proyecto.ui.HuertaInput
import com.example.proyecto.ui.garden.GardenViewModel
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.util.MediaManager
import kotlinx.datetime.*
import org.jetbrains.compose.resources.stringResource
import huertomanager.composeapp.generated.resources.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddDiaryEntryScreen(
    navController: NavController,
    initialDateMillis: Long,
    taskId: String? = null,
    initialTitle: String? = null,
    initialDesc: String? = null,
    initialGarden: String? = null,
    initialType: String? = null,
    initialWater: Float = 0f,
    initialIsUrgent: Boolean = false,
    viewModel: GardenViewModel = koinViewModel()
) {
    val isEditMode = taskId != null
    val scrollState = rememberScrollState()

    // --- RECURSOS (Fuera de lambdas para evitar errores de contexto) ---
    val irrigationTypeStr = stringResource(Res.string.diary_chip_irrigation)
    val successMsg = stringResource(Res.string.dialog_success_diary_saved)

    // --- ESTADOS ---
    var title by remember { mutableStateOf(initialTitle ?: "") }
    var description by remember { mutableStateOf(initialDesc ?: "") }
    var selectedDate by remember {
        mutableStateOf(if (initialDateMillis > 0) Instant.fromEpochMilliseconds(initialDateMillis).toLocalDateTime(TimeZone.currentSystemDefault()).date else Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
    }

    // Lógica de Jardineras y Bancales
    val jardineras by viewModel.jardineras.collectAsState()
    var selectedJardinera by remember { mutableStateOf<JardineraEntity?>(null) }
    var expandedGarden by remember { mutableStateOf(false) }
    val selectedBancalIds = remember { mutableStateListOf<Long>() }

    // Obtenemos los bancales de la jardinera seleccionada
    val bancalesDisponibles by if (selectedJardinera != null) {
        viewModel.getBancales(selectedJardinera!!.id).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList<BancalEntity>()) }
    }

    val taskTypes = listOf(
        irrigationTypeStr,
        stringResource(Res.string.diary_chip_pruning),
        stringResource(Res.string.diary_chip_harvest),
        stringResource(Res.string.diary_chip_fertilizer),
        stringResource(Res.string.diary_chip_other)
    )
    var selectedType by remember { mutableStateOf(initialType ?: taskTypes[0]) }
    var waterAmount by remember { mutableStateOf(initialWater) }
    var isUrgent by remember { mutableStateOf(initialIsUrgent) }

    // Cámara
    var diaryPhotoBytes by remember { mutableStateOf<ByteArray?>(null) }
    var showPhotoOptions by remember { mutableStateOf(false) }
    val launcher = MediaManager.rememberLauncher { bytes ->
        if (bytes != null) diaryPhotoBytes = bytes
        showPhotoOptions = false
    }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(if (isEditMode) Res.string.menu_edit else Res.string.add_diary_title)) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(scrollState).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // --- SECCIÓN 1: DÓNDE Y CUÁNDO ---
            OutlinedCard(border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Ubicación y Fecha", color = GreenPrimary, fontWeight = FontWeight.Bold)

                    Box {
                        SelectorRow(
                            label = "Seleccionar Jardinera",
                            value = selectedJardinera?.nombre ?: "Toca para elegir",
                            icon = Icons.Filled.Grass
                        ) { expandedGarden = true }

                        DropdownMenu(expanded = expandedGarden, onDismissRequest = { expandedGarden = false }) {
                            jardineras.forEach { jardinera ->
                                DropdownMenuItem(
                                    text = { Text(jardinera.nombre) },
                                    onClick = {
                                        selectedJardinera = jardinera
                                        selectedBancalIds.clear() // Reset al cambiar de jardinera
                                        expandedGarden = false
                                    }
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    SelectorRow(
                        label = "Fecha de actividad",
                        value = "${selectedDate.dayOfMonth}/${selectedDate.monthNumber}/${selectedDate.year}",
                        icon = Icons.Filled.CalendarToday
                    ) { showDatePicker = true }
                }
            }

            // --- NUEVA SECCIÓN: SELECTOR VISUAL DE BANCALES ---
            if (selectedJardinera != null) {
                OutlinedCard(border = BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.3f))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Selecciona los bancales", fontWeight = FontWeight.Bold)
                            TextButton(onClick = {
                                if (selectedBancalIds.size == bancalesDisponibles.size) selectedBancalIds.clear()
                                else {
                                    selectedBancalIds.clear()
                                    selectedBancalIds.addAll(bancalesDisponibles.map { it.id })
                                }
                            }) {
                                Text(if (selectedBancalIds.size == bancalesDisponibles.size) "Deseleccionar" else "Todos")
                            }
                        }

                        // Reutilizamos la lógica de rejilla de la Jardinera
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(selectedJardinera!!.columnas),
                            modifier = Modifier.heightIn(max = 250.dp).padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(bancalesDisponibles) { bancal ->
                                val isSelected = selectedBancalIds.contains(bancal.id)
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) GreenPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable {
                                            if (isSelected) selectedBancalIds.remove(bancal.id)
                                            else selectedBancalIds.add(bancal.id)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = bancal.nombreCultivo?.take(2) ?: "${bancal.fila}-${bancal.columna}",
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- SECCIÓN 2: DETALLES DE LA TAREA ---
            OutlinedCard(border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text("Detalles", color = GreenPrimary, fontWeight = FontWeight.Bold)
                    HuertaInput(title, { title = it }, "Título de la actividad", Icons.Filled.Title)
                    HuertaInput(description, { description = it }, "Notas adicionales", Icons.Filled.Description)

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Tipo", style = MaterialTheme.typography.labelLarge)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            taskTypes.forEach { type ->
                                FilterChip(
                                    selected = (type == selectedType),
                                    onClick = { selectedType = type },
                                    label = { Text(type) }
                                )
                            }
                        }
                    }

                    if (selectedType == irrigationTypeStr) {
                        Column(modifier = Modifier.background(GreenPrimary.copy(alpha = 0.05f), RoundedCornerShape(12.dp)).padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Cantidad de agua")
                                Text("${waterAmount.toInt()} L", fontWeight = FontWeight.Bold, color = GreenPrimary)
                            }
                            Slider(value = waterAmount, onValueChange = { waterAmount = it }, valueRange = 0f..20f, steps = 19)
                        }
                    }
                }
            }

            // --- SECCIÓN 3: FOTO Y URGENCIA ---
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(
                    modifier = Modifier.weight(1f).height(100.dp).clip(RoundedCornerShape(16.dp)).clickable { showPhotoOptions = true },
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, if (diaryPhotoBytes != null) GreenPrimary else Color.Transparent)
                ) {
                    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(if (diaryPhotoBytes != null) Icons.Outlined.Image else Icons.Outlined.CameraAlt, null, tint = if (diaryPhotoBytes != null) GreenPrimary else Color.Gray)
                        Text(if (diaryPhotoBytes != null) "Foto añadida" else "Cámara", fontSize = 12.sp)
                    }
                }

                Card(
                    modifier = Modifier.weight(0.7f).height(100.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isUrgent) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, if(isUrgent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                        Text("¿Urgente?", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Switch(checked = isUrgent, onCheckedChange = { isUrgent = it }, modifier = Modifier.align(Alignment.End))
                    }
                }
            }

            // --- BOTÓN GUARDAR ---
            Button(
                onClick = {
                    if (title.isNotBlank() && selectedBancalIds.isNotEmpty()) {
                        val finalDesc = if (selectedType == irrigationTypeStr) "$title - ${waterAmount.toInt()}L" else description

                        selectedBancalIds.forEach { bId ->
                            viewModel.guardarEntradaDiario(
                                bancalId = bId,
                                tipo = selectedType,
                                desc = finalDesc,
                                fecha = selectedDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
                                id = taskId?.toLongOrNull() ?: 0L // FIX: Pasa el ID si existe para actualizar
                            )
                        }
                        showSuccessDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = selectedBancalIds.isNotEmpty() && title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text(if(isEditMode) "Actualizar Tarea" else "Registrar en ${selectedBancalIds.size} bancales", fontWeight = FontWeight.Bold)
            }
        }
    }

    // --- DIÁLOGOS ---
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("¡Éxito!") },
            text = { Text(successMsg) },
            confirmButton = {
                Button(onClick = { showSuccessDialog = false; navController.popBackStack() }) {
                    Text("Aceptar")
                }
            }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = datePickerState) }
    }
}

@Composable
fun SelectorRow(label: String, value: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            }
        }
        Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
    }
}