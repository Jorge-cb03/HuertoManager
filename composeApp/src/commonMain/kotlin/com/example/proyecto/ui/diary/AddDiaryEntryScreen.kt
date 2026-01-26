package com.example.proyecto.ui.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.proyecto.ui.HuertaInput
import com.example.proyecto.ui.theme.GreenPrimary
import kotlinx.datetime.*
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddDiaryEntryScreen(navController: NavController, initialDateMillis: Long) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // ALERTA ÉXITO
    var showSuccessDialog by remember { mutableStateOf(false) }

    var selectedDate by remember {
        mutableStateOf(if (initialDateMillis > 0) Instant.fromEpochMilliseconds(initialDateMillis).toLocalDateTime(TimeZone.currentSystemDefault()).date else Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
    }
    var showDatePicker by remember { mutableStateOf(false) }
    val gardens = listOf("Invernadero", "Terraza", "Cama Alta", "Macetas")
    var selectedGarden by remember { mutableStateOf(gardens[0]) }
    var expandedGarden by remember { mutableStateOf(false) }
    val taskTypes = listOf(stringResource(Res.string.diary_chip_irrigation), stringResource(Res.string.diary_chip_pruning), stringResource(Res.string.diary_chip_harvest), stringResource(Res.string.diary_chip_fertilizer), stringResource(Res.string.diary_chip_other))
    var selectedType by remember { mutableStateOf(taskTypes[0]) }
    var waterAmount by remember { mutableStateOf(0f) }
    var isUrgent by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.add_diary_title)) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            // JARDINERA
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = selectedGarden, onValueChange = {}, readOnly = true, label = { Text(stringResource(Res.string.diary_select_garden)) }, leadingIcon = { Icon(Icons.Filled.Grass, null) }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGarden) }, modifier = Modifier.fillMaxWidth().clickable { expandedGarden = true }, enabled = false, colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface, disabledBorderColor = MaterialTheme.colorScheme.outline, disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant, disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant, disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant))
                Box(Modifier.matchParentSize().clickable { expandedGarden = true })
                DropdownMenu(expanded = expandedGarden, onDismissRequest = { expandedGarden = false }, modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    gardens.forEach { garden -> DropdownMenuItem(text = { Text(garden) }, onClick = { selectedGarden = garden; expandedGarden = false }) }
                }
            }

            // FECHA
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = "${selectedDate.dayOfMonth}/${selectedDate.monthNumber}/${selectedDate.year}", onValueChange = {}, readOnly = true, label = { Text(stringResource(Res.string.diary_date_label)) }, leadingIcon = { Icon(Icons.Filled.CalendarToday, null) }, modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }, enabled = false, colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface, disabledBorderColor = MaterialTheme.colorScheme.outline, disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant, disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant))
                Box(Modifier.matchParentSize().clickable { showDatePicker = true })
            }

            HuertaInput(title, { title = it }, stringResource(Res.string.add_diary_task), Icons.Filled.Title)
            HuertaInput(description, { description = it }, stringResource(Res.string.add_diary_desc), Icons.Filled.Description)

            // CHIPS
            Text(stringResource(Res.string.diary_type), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                taskTypes.forEach { type -> FilterChip(selected = (type == selectedType), onClick = { selectedType = type }, label = { Text(type) }, leadingIcon = if (type == selectedType) { { Icon(Icons.Filled.Check, null, modifier = Modifier.size(16.dp)) } } else null) }
            }

            if (selectedType == stringResource(Res.string.diary_chip_irrigation)) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(stringResource(Res.string.diary_slider_label), fontSize = 14.sp); Text("${waterAmount.toInt()} L", fontWeight = FontWeight.Bold, color = GreenPrimary) }
                    Slider(value = waterAmount, onValueChange = { waterAmount = it }, valueRange = 0f..20f, steps = 19, colors = SliderDefaults.colors(thumbColor = GreenPrimary, activeTrackColor = GreenPrimary))
                }
            }

            Row(modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp)).padding(16.dp).clickable { }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Icon(Icons.Filled.CameraAlt, null, tint = MaterialTheme.colorScheme.secondary); Spacer(Modifier.width(8.dp)); Text(stringResource(Res.string.diary_add_photo), color = MaterialTheme.colorScheme.secondary)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(Res.string.diary_switch_urgent), fontWeight = FontWeight.Bold); Switch(checked = isUrgent, onCheckedChange = { isUrgent = it }, colors = SwitchDefaults.colors(checkedThumbColor = GreenPrimary, checkedTrackColor = GreenPrimary.copy(alpha=0.5f)))
            }

            Spacer(Modifier.height(20.dp))
            Button(onClick = { showSuccessDialog = true }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) {
                Text(stringResource(Res.string.add_diary_btn))
            }
            Spacer(Modifier.height(40.dp))
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { millis -> selectedDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date }; showDatePicker = false }) { Text("OK") } }, dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }) { DatePicker(state = datePickerState) }
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text(stringResource(Res.string.dialog_success_title)) },
                text = { Text(stringResource(Res.string.dialog_success_diary_saved)) },
                confirmButton = {
                    Button(onClick = { showSuccessDialog = false; navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) { Text(stringResource(Res.string.dialog_btn_ok)) }
                }
            )
        }
    }
}