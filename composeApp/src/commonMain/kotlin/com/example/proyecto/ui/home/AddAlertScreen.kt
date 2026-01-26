package com.example.proyecto.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto.di.AppModule
import com.example.proyecto.domain.model.TipoEvento
import com.example.proyecto.ui.diary.AddDiaryViewModel
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlertScreen(
    navController: NavController,
    // Usamos el mismo ViewModel de guardado porque la lógica de BD es la misma,
    // pero la UI es específica para Alertas
    viewModel: AddDiaryViewModel = viewModel { AddDiaryViewModel(AppModule.huertaRepository) }
) {
    val focusManager = LocalFocusManager.current

    // Configuración Inicial: Mañana a las 09:00 por defecto (Es una alerta futura)
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    var selectedDate by remember { mutableStateOf(now.date) }
    var selectedTime by remember { mutableStateOf(LocalTime(9, 0)) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TipoEvento.NOTA) }

    // Control UI
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var expandedTypeMenu by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(initialHour = 9, initialMinute = 0, is24Hour = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Recordatorio", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.Close, null) } },
                actions = {
                    Button(
                        onClick = {
                            val finalDateTime = LocalDateTime(selectedDate, selectedTime)
                            viewModel.saveEntry(title, description, selectedType, finalDateTime)
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                        enabled = title.isNotBlank()
                    ) {
                        Text("Programar")
                    }
                }
            )
        }
    ) { padding ->
        // Bloque para ocultar teclado al pulsar fuera
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
        ) {
            Column(modifier = Modifier.padding(20.dp).fillMaxSize()) {

                // 1. SELECTOR DE TIPO (DESPLEGABLE)
                ExposedDropdownMenuBox(
                    expanded = expandedTypeMenu,
                    onExpandedChange = { expandedTypeMenu = !expandedTypeMenu },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedType.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Alerta") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTypeMenu) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Filled.Notifications, null) }
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTypeMenu,
                        onDismissRequest = { expandedTypeMenu = false }
                    ) {
                        // Filtramos tipos que tengan sentido para alertas
                        listOf(TipoEvento.NOTA, TipoEvento.RIEGO, TipoEvento.TRATAMIENTO, TipoEvento.FERTILIZANTE).forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo.name) },
                                onClick = { selectedType = tipo; expandedTypeMenu = false }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // 2. FECHA Y HORA (BOTONES)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SelectorButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.CalendarToday,
                        label = "Día",
                        value = "${selectedDate.dayOfMonth}/${selectedDate.monthNumber}",
                        onClick = { showDatePicker = true }
                    )
                    SelectorButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.Schedule,
                        label = "Hora",
                        value = "${selectedTime.hour.toString().padStart(2,'0')}:${selectedTime.minute.toString().padStart(2,'0')}",
                        onClick = { showTimePicker = true }
                    )
                }

                Spacer(Modifier.height(20.dp))

                // 3. CAMPOS DE TEXTO
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título del recordatorio") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Notas (Opcional)") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    maxLines = 3
                )
            }
        }

        // DIALOGOS
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val instant = Instant.fromEpochMilliseconds(millis)
                            selectedDate = instant.toLocalDateTime(TimeZone.UTC).date
                        }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
            ) { DatePicker(state = datePickerState) }
        }

        if (showTimePicker) {
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        selectedTime = LocalTime(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }) { Text("OK") }
                },
                dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") } },
                text = { TimePicker(state = timePickerState) }
            )
        }
    }
}

@Composable
fun SelectorButton(modifier: Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}