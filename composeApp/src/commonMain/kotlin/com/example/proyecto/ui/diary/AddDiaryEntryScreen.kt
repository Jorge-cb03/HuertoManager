package com.example.proyecto.ui.diary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.proyecto.ui.HuertaInput
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.util.MediaManager
import kotlinx.datetime.*
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

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
    initialIsUrgent: Boolean = false
) {
    val isEditMode = taskId != null
    var title by remember { mutableStateOf(initialTitle ?: "") }
    var description by remember { mutableStateOf(initialDesc ?: "") }
    var selectedDate by remember {
        mutableStateOf(if (initialDateMillis > 0) Instant.fromEpochMilliseconds(initialDateMillis).toLocalDateTime(TimeZone.currentSystemDefault()).date else Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
    }

    val gardens = listOf("Invernadero", "Terraza", "Cama Alta", "Macetas")
    var selectedGarden by remember { mutableStateOf(initialGarden ?: gardens[0]) }
    var expandedGarden by remember { mutableStateOf(false) }

    val taskTypes = listOf(
        stringResource(Res.string.diary_chip_irrigation),
        stringResource(Res.string.diary_chip_pruning),
        stringResource(Res.string.diary_chip_harvest),
        stringResource(Res.string.diary_chip_fertilizer),
        stringResource(Res.string.diary_chip_other)
    )
    var selectedType by remember { mutableStateOf(initialType ?: taskTypes[0]) }
    var waterAmount by remember { mutableStateOf(initialWater) }
    var isUrgent by remember { mutableStateOf(initialIsUrgent) }

    // --- FUNCIONALIDAD DE CÁMARA RESTAURADA ---
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
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // --- SECCIÓN 1: DÓNDE Y CUÁNDO ---
            OutlinedCard(
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(stringResource(Res.string.section_where_when), style = MaterialTheme.typography.titleMedium, color = GreenPrimary, fontWeight = FontWeight.Bold)

                    // Selector de Jardinera (Corregido con Box para mejor posicionamiento del menú)
                    Box {
                        SelectorRow(
                            label = stringResource(Res.string.diary_select_garden),
                            value = selectedGarden,
                            icon = Icons.Filled.Grass,
                            onClick = { expandedGarden = true }
                        )
                        DropdownMenu(
                            expanded = expandedGarden,
                            onDismissRequest = { expandedGarden = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface).width(200.dp)
                        ) {
                            gardens.forEach { garden ->
                                DropdownMenuItem(
                                    text = { Text(garden) },
                                    onClick = { selectedGarden = garden; expandedGarden = false },
                                    leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = GreenPrimary, modifier = Modifier.size(18.dp)) }
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Selector de Fecha
                    SelectorRow(
                        label = stringResource(Res.string.diary_date_label),
                        value = "${selectedDate.dayOfMonth}/${selectedDate.monthNumber}/${selectedDate.year}",
                        icon = Icons.Filled.CalendarToday,
                        onClick = { showDatePicker = true }
                    )
                }
            }

            // --- SECCIÓN 2: DETALLES DE LA TAREA ---
            OutlinedCard(
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text(stringResource(Res.string.section_details), style = MaterialTheme.typography.titleMedium, color = GreenPrimary, fontWeight = FontWeight.Bold)

                    HuertaInput(title, { title = it }, stringResource(Res.string.add_diary_task), Icons.Filled.Title)
                    HuertaInput(description, { description = it }, stringResource(Res.string.add_diary_desc), Icons.Filled.Description)

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(stringResource(Res.string.diary_type), style = MaterialTheme.typography.labelLarge)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            taskTypes.forEach { type ->
                                FilterChip(
                                    selected = (type == selectedType),
                                    onClick = { selectedType = type },
                                    label = { Text(type) },
                                    leadingIcon = if (type == selectedType) { { Icon(Icons.Filled.Check, null, modifier = Modifier.size(18.dp)) } } else null,
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GreenPrimary.copy(alpha = 0.2f), selectedLabelColor = GreenPrimary, selectedLeadingIconColor = GreenPrimary)
                                )
                            }
                        }
                    }

                    if (selectedType == stringResource(Res.string.diary_chip_irrigation)) {
                        Column(modifier = Modifier.background(GreenPrimary.copy(alpha = 0.05f), RoundedCornerShape(12.dp)).padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.WaterDrop, null, tint = GreenPrimary)
                                    Spacer(Modifier.width(8.dp))
                                    Text(stringResource(Res.string.diary_slider_label), fontSize = 14.sp)
                                }
                                Text("${waterAmount.toInt()} L", fontWeight = FontWeight.Bold, color = GreenPrimary, fontSize = 18.sp)
                            }
                            Slider(value = waterAmount, onValueChange = { waterAmount = it }, valueRange = 0f..20f, steps = 19, colors = SliderDefaults.colors(thumbColor = GreenPrimary, activeTrackColor = GreenPrimary))
                        }
                    }
                }
            }

            // --- SECCIÓN 3: FOTO Y URGENCIA ---
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(
                    modifier = Modifier.weight(1f).height(100.dp).clip(RoundedCornerShape(16.dp)).clickable { showPhotoOptions = true },
                    color = if (diaryPhotoBytes != null) GreenPrimary.copy(0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, if (diaryPhotoBytes != null) GreenPrimary else MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            if (diaryPhotoBytes != null) Icons.Outlined.Image else Icons.Outlined.CameraAlt,
                            null,
                            tint = if (diaryPhotoBytes != null) GreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            if (diaryPhotoBytes != null) stringResource(Res.string.photo_attached_label) else stringResource(Res.string.diary_add_photo),
                            fontSize = 12.sp,
                            color = if (diaryPhotoBytes != null) GreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(0.7f).height(100.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isUrgent) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, if(isUrgent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                        Text(stringResource(Res.string.diary_switch_urgent), fontWeight = FontWeight.Bold, color = if(isUrgent) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface)
                        Switch(
                            checked = isUrgent,
                            onCheckedChange = { isUrgent = it },
                            modifier = Modifier.align(Alignment.End),
                            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.error, checkedTrackColor = MaterialTheme.colorScheme.error.copy(0.5f))
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = { showSuccessDialog = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(stringResource(if (isEditMode) Res.string.profile_edit_save else Res.string.add_diary_btn), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(40.dp))
        }

        // --- DIÁLOGOS RESTAURADOS ---

        // 1. Selector de Foto
        if (showPhotoOptions) {
            AlertDialog(
                onDismissRequest = { showPhotoOptions = false },
                title = { Text(stringResource(Res.string.profile_change_photo)) },
                text = {
                    Column {
                        ListItem(
                            headlineContent = { Text("Cámara") },
                            leadingContent = { Icon(Icons.Default.PhotoCamera, null, tint = GreenPrimary) },
                            modifier = Modifier.clickable { launcher.launchCamera() }
                        )
                        ListItem(
                            headlineContent = { Text("Galería") },
                            leadingContent = { Icon(Icons.Default.PhotoLibrary, null, tint = GreenPrimary) },
                            modifier = Modifier.clickable { launcher.launchGallery() }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showPhotoOptions = false }) { Text(stringResource(Res.string.dialog_cancel)) }
                }
            )
        }

        // 2. Calendario
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = selectedDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date
                        }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text(stringResource(Res.string.dialog_cancel)) }
                }
            ) { DatePicker(state = datePickerState) }
        }

        // 3. Éxito
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text(stringResource(Res.string.dialog_success_title)) },
                text = { Text(stringResource(Res.string.dialog_success_diary_saved)) },
                confirmButton = {
                    Button(
                        onClick = { showSuccessDialog = false; navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) { Text(stringResource(Res.string.dialog_btn_ok)) }
                }
            )
        }
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
        Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}