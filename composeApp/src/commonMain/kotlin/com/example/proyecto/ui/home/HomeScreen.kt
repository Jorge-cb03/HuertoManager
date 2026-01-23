package com.example.proyecto.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.proyecto.ui.HuertaCard
import com.example.proyecto.ui.HuertaInput
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.GreenSecondary
import com.example.proyecto.ui.theme.RedDanger
import com.example.proyecto.util.NotificationManager // Para notificaciones reales
import kotlinx.datetime.*
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

// Modelo para manejar alertas dinámicas
data class AlertUiModel(
    val id: Long = Clock.System.now().toEpochMilliseconds(),
    var title: String,
    var date: LocalDate,
    val isUrgent: Boolean
)

@Composable
fun HomeScreen(navController: NavController? = null) {
    var showAlertDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    var alertToDelete by remember { mutableStateOf<AlertUiModel?>(null) }
    var alertToEdit by remember { mutableStateOf<AlertUiModel?>(null) }

    val alerts = remember {
        mutableStateListOf(
            AlertUiModel(1, "Riego Necesario", LocalDate(2026, 1, 25), true),
            AlertUiModel(2, "Revisión Mensual", LocalDate(2026, 2, 1), false)
        )
    }

    // Ordenación por fecha más próxima
    val sortedAlerts = alerts.sortedBy { it.date }
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("${today.dayOfMonth}/${today.monthNumber}", color = MaterialTheme.colorScheme.secondary)
                Text(
                    "${stringResource(Res.string.home_greeting)}\n${stringResource(Res.string.home_role)}",
                    fontSize = 32.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground
                )
            }

            IconButton(
                onClick = {
                    alertToEdit = null
                    showAlertDialog = true
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)
            ) {
                Icon(Icons.Filled.Notifications, null, tint = GreenPrimary)
            }
        }

        // --- TARJETA SALUD ---
        HuertaCard(modifier = Modifier.height(200.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(stringResource(Res.string.home_status_label), color = GreenSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text(stringResource(Res.string.home_health_title), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                }
                Box(modifier = Modifier.size(60.dp).border(4.dp, GreenSecondary, CircleShape), contentAlignment = Alignment.Center) {
                    Text("85%", color = GreenSecondary, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.weight(1f))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                WeatherItem(Icons.Filled.WaterDrop, "60%", stringResource(Res.string.weather_humidity))
                WeatherItem(Icons.Filled.WbSunny, "24°C", stringResource(Res.string.weather_temp))
                WeatherItem(Icons.Filled.Air, "Alto", stringResource(Res.string.weather_uv))
            }
        }

        Spacer(Modifier.height(20.dp))
        Text(stringResource(Res.string.home_alerts_title), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(10.dp))

        // --- LISTADO DE ALERTAS ---
        sortedAlerts.forEach { alert ->
            AlertCardWithMenu(
                alert = alert,
                onEdit = {
                    alertToEdit = alert
                    showAlertDialog = true
                },
                onDelete = {
                    alertToDelete = alert
                    showDeleteConfirm = true
                }
            )
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(80.dp))
    }

    // Diálogo de Añadir / Editar
    if (showAlertDialog) {
        AlertSchedulerDialog(
            initialName = alertToEdit?.title ?: "",
            initialDate = alertToEdit?.date ?: today,
            onDismiss = { showAlertDialog = false },
            onSave = { name, type, date, hour, minute ->
                // Cálculo del momento exacto para la notificación
                val localDateTime = LocalDateTime(date.year, date.monthNumber, date.dayOfMonth, hour, minute)
                val epochSeconds = localDateTime.toInstant(TimeZone.currentSystemDefault()).epochSeconds

                if (alertToEdit != null) {
                    val index = alerts.indexOfFirst { it.id == alertToEdit!!.id }
                    if (index != -1) {
                        alerts[index] = alerts[index].copy(title = name, date = date)
                    }
                } else {
                    val newAlert = AlertUiModel(title = name, date = date, isUrgent = type.lowercase().contains("riego"))
                    alerts.add(newAlert)
                    // Programación real
                    NotificationManager.scheduleNotification(name, "Aviso: $type", epochSeconds)
                }
                showAlertDialog = false
                alertToEdit = null
            }
        )
    }

    // Diálogo de Confirmación Borrado
    if (showDeleteConfirm && alertToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(Res.string.menu_delete)) },
            text = { Text("¿Seguro que quieres borrar '${alertToDelete?.title}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        alerts.remove(alertToDelete)
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDanger)
                ) { Text(stringResource(Res.string.dialog_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text(stringResource(Res.string.dialog_cancel)) }
            }
        )
    }
}

@Composable
fun AlertCardWithMenu(alert: AlertUiModel, onEdit: () -> Unit, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    HuertaCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if(alert.isUrgent) Icons.Filled.WaterDrop else Icons.Filled.Notifications,
                    null,
                    tint = if(alert.isUrgent) RedDanger else GreenPrimary
                )
                Column(Modifier.padding(start = 16.dp)) {
                    Text(alert.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Text("${alert.date.dayOfMonth}/${alert.date.monthNumber}/${alert.date.year}", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                }
            }

            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, null, tint = MaterialTheme.colorScheme.secondary)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.menu_edit)) },
                        onClick = { expanded = false; onEdit() },
                        leadingIcon = { Icon(Icons.Default.Edit, null) }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.menu_delete)) },
                        onClick = { expanded = false; onDelete() },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = RedDanger) }
                    )
                }
            }
        }
    }
}

@Composable
fun AlertSchedulerDialog(
    initialName: String = "",
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onSave: (String, String, LocalDate, Int, Int) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var type by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(initialDate) }

    // Estados de Hora
    var hour by remember { mutableStateOf(10) }
    var minute by remember { mutableStateOf(0) }

    // Estados de Calendario
    var currentMonthDate by remember { mutableStateOf(LocalDate(selectedDate.year, selectedDate.monthNumber, 1)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.alert_dialog_title)) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                HuertaInput(name, { name = it }, stringResource(Res.string.alert_name_hint), Icons.Filled.Label)
                Spacer(Modifier.height(10.dp))
                HuertaInput(type, { type = it }, stringResource(Res.string.alert_type_hint), Icons.Filled.Category)

                Spacer(Modifier.height(20.dp))
                Text("Hora de notificación:", fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = { if(hour > 0) hour-- }) { Icon(Icons.Filled.Remove, null) }
                    Text("${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { if(hour < 23) hour++ }) { Icon(Icons.Filled.Add, null) }
                }

                Spacer(Modifier.height(20.dp))
                Text(stringResource(Res.string.alert_date_label), fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))

                // Selector Mes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { currentMonthDate = currentMonthDate.minus(1, DateTimeUnit.MONTH) }) {
                        Icon(Icons.Filled.ChevronLeft, null)
                    }
                    Text("${currentMonthDate.monthNumber}/${currentMonthDate.year}", fontWeight = FontWeight.Bold)
                    IconButton(onClick = { currentMonthDate = currentMonthDate.plus(1, DateTimeUnit.MONTH) }) {
                        Icon(Icons.Filled.ChevronRight, null)
                    }
                }

                // Grid de días recuperado
                val daysInMonth = currentMonthDate.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY).dayOfMonth
                val startDayOfWeek = currentMonthDate.dayOfWeek.ordinal

                var currentDayCounter = 1
                val totalCells = startDayOfWeek + daysInMonth
                val rows = (totalCells + 6) / 7

                Column {
                    for (r in 0 until rows) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            for (c in 0 until 7) {
                                val index = r * 7 + c
                                if (index >= startDayOfWeek && currentDayCounter <= daysInMonth) {
                                    val date = LocalDate(currentMonthDate.year, currentMonthDate.monthNumber, currentDayCounter)
                                    val isSelected = date == selectedDate

                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) GreenPrimary else Color.Transparent)
                                            .clickable { selectedDate = date }
                                    ) {
                                        Text(
                                            "$currentDayCounter",
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    currentDayCounter++
                                } else {
                                    Spacer(Modifier.size(32.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, type, selectedDate, hour, minute) },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) { Text(stringResource(Res.string.alert_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.alert_cancel)) }
        }
    )
}

@Composable
fun WeatherItem(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.secondary)
        Text(value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
    }
}