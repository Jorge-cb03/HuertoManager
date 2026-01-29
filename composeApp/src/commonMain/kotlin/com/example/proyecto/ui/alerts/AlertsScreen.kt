package com.example.proyecto.ui.alerts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.ui.HuertaCard
import com.example.proyecto.ui.HuertaInput
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.RedDanger
import com.example.proyecto.util.NotificationManager
import kotlinx.datetime.*
import org.jetbrains.compose.resources.stringResource
import huertomanager.composeapp.generated.resources.*

@Immutable
data class AlertUiModel(
    val id: Long = Clock.System.now().toEpochMilliseconds(),
    var title: String,
    var description: String,
    var dateTime: LocalDateTime,
    val isUrgent: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(navController: NavController) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingAlert by remember { mutableStateOf<AlertUiModel?>(null) }
    val alerts = remember { mutableStateListOf<AlertUiModel>() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.alerts_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = GreenPrimary) {
                Icon(Icons.Default.Add, null, tint = Color.White)
            }
        }
    ) { padding ->
        // ESPACIADO MEJORADO: Arrangement.spacedBy y contentPadding
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp)
        ) {
            items(alerts.sortedBy { it.dateTime }) { alert ->
                AlertItemCard(
                    alert = alert,
                    onEdit = { editingAlert = alert },
                    onDelete = { alerts.remove(alert) }
                )
            }
        }
    }

    if (showAddDialog || editingAlert != null) {
        AlertCreationDialog(
            initialAlert = editingAlert,
            onDismiss = { showAddDialog = false; editingAlert = null },
            onSave = { name, desc, date, hour, minute ->
                val dt = LocalDateTime(date.year, date.monthNumber, date.dayOfMonth, hour, minute)

                // --- CONVERSIÓN A EPOCH SECONDS PARA EL SISTEMA ---
                val epochSeconds = dt.toInstant(TimeZone.currentSystemDefault()).epochSeconds

                // --- PROGRAMAR NOTIFICACIÓN REAL ---
                NotificationManager.scheduleNotification(
                    title = name,
                    message = desc,
                    epochSeconds = epochSeconds
                )

                if (editingAlert != null) {
                    val index = alerts.indexOf(editingAlert)
                    if (index != -1) alerts[index] = editingAlert!!.copy(title = name, description = desc, dateTime = dt)
                } else {
                    alerts.add(AlertUiModel(title = name, description = desc, dateTime = dt, isUrgent = false))
                }
                showAddDialog = false; editingAlert = null
            }
        )
    }
}

@Composable
fun AlertItemCard(alert: AlertUiModel, onEdit: () -> Unit, onDelete: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    HuertaCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = GreenPrimary.copy(alpha = 0.1f), modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Notifications, null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(alert.title, fontWeight = FontWeight.Bold)
                if (alert.description.isNotEmpty()) {
                    Text(alert.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary, maxLines = 1)
                }
                Text("${alert.dateTime.dayOfMonth}/${alert.dateTime.monthNumber} - ${alert.dateTime.hour.toString().padStart(2,'0')}:${alert.dateTime.minute.toString().padStart(2,'0')}", fontSize = 11.sp)
            }
            // MENÚ DE 3 PUNTOS
            Box {
                IconButton(onClick = { showMenu = true }) { Icon(Icons.Default.MoreVert, null) }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.menu_edit)) },
                        leadingIcon = { Icon(Icons.Default.Edit, null) },
                        onClick = { showMenu = false; onEdit() }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.menu_delete), color = RedDanger) },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = RedDanger) },
                        onClick = { showMenu = false; onDelete() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertCreationDialog(initialAlert: AlertUiModel?, onDismiss: () -> Unit, onSave: (String, String, LocalDate, Int, Int) -> Unit) {
    var name by remember { mutableStateOf(initialAlert?.title ?: "") }
    var desc by remember { mutableStateOf(initialAlert?.description ?: "") }
    var selectedDate by remember { mutableStateOf(initialAlert?.dateTime?.date ?: Clock.System.todayIn(TimeZone.currentSystemDefault())) }
    var hour by remember { mutableStateOf(initialAlert?.dateTime?.hour ?: 10) }
    var minute by remember { mutableStateOf(initialAlert?.dateTime?.minute ?: 0) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { selectedDate = Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date }; showDatePicker = false }) { Text(stringResource(Res.string.dialog_btn_ok)) } }
        ) { DatePicker(state = datePickerState) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(if(initialAlert == null) Res.string.alert_dialog_title else Res.string.menu_edit)) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                HuertaInput(name, { name = it }, stringResource(Res.string.alert_name_hint), Icons.Default.Label)
                Spacer(Modifier.height(8.dp))
                HuertaInput(desc, { desc = it }, stringResource(Res.string.add_diary_desc), Icons.Default.Description)
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.CalendarMonth, null)
                    Spacer(Modifier.width(8.dp))
                    Text("${selectedDate.dayOfMonth}/${selectedDate.monthNumber}/${selectedDate.year}")
                }

                Spacer(Modifier.height(20.dp))

                // SELECTOR DE TIEMPO CON MINUTOS MOVILES
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                    // Columna Horas
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Hora", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if(hour > 0) hour-- else hour = 23 }) { Icon(Icons.Default.Remove, null) }
                            Text(hour.toString().padStart(2, '0'), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { if(hour < 23) hour++ else hour = 0 }) { Icon(Icons.Default.Add, null) }
                        }
                    }

                    Text(":", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 15.dp))

                    // Columna Minutos
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Minutos", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if(minute > 0) minute -= 5 else minute = 55 }) { Icon(Icons.Default.Remove, null) }
                            Text(minute.toString().padStart(2, '0'), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { if(minute < 55) minute += 5 else minute = 0 }) { Icon(Icons.Default.Add, null) }
                        }
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { onSave(name, desc, selectedDate, hour, minute) }) { Text(stringResource(Res.string.alert_save)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(Res.string.alert_cancel)) } }
    )
}