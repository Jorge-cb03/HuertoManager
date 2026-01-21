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
import com.example.proyecto.ui.theme.GreenPrimary   // TU COLOR
import com.example.proyecto.ui.theme.GreenSecondary // TU COLOR
import com.example.proyecto.ui.theme.RedDanger      // TU COLOR
import kotlinx.datetime.*
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

@Composable
fun HomeScreen(navController: NavController? = null) {
    var showAlertDialog by remember { mutableStateOf(false) }
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // HEADER
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

            // --- BOTÓN CAMPANA (Abre Diálogo) ---
            IconButton(
                onClick = { showAlertDialog = true },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)
            ) {
                // Si quieres que parezca que hay alerta, usa RedDanger, si no GreenPrimary
                Icon(Icons.Filled.Notifications, null, tint = GreenPrimary)
            }
        }

        // --- TARJETA SALUD (Usa GreenSecondary) ---
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

        AlertCard(stringResource(Res.string.alert_irrigation_title), stringResource(Res.string.alert_irrigation_desc), true)
        Spacer(Modifier.height(10.dp))
        AlertCard(stringResource(Res.string.alert_review_title), stringResource(Res.string.alert_review_desc), false)

        Spacer(Modifier.height(80.dp))
    }

    // --- DIÁLOGO DE ALERTA ---
    if (showAlertDialog) {
        AlertSchedulerDialog(
            onDismiss = { showAlertDialog = false },
            onSave = { name, type, date ->
                showAlertDialog = false
                // Aquí guardarías la alerta
            }
        )
    }
}

@Composable
fun AlertSchedulerDialog(onDismiss: () -> Unit, onSave: (String, String, LocalDate) -> Unit) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }

    // Estado Calendario
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    var selectedDate by remember { mutableStateOf(today) }

    // Control mes visual
    var currentMonthDate by remember { mutableStateOf(LocalDate(today.year, today.monthNumber, 1)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.alert_dialog_title)) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                HuertaInput(name, { name = it }, stringResource(Res.string.alert_name_hint), Icons.Filled.Label)
                Spacer(Modifier.height(10.dp))
                HuertaInput(type, { type = it }, stringResource(Res.string.alert_type_hint), Icons.Filled.Category)

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

                // Grid simple de días (7 columnas)
                val daysInMonth = currentMonthDate.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY).dayOfMonth
                val startDayOfWeek = currentMonthDate.dayOfWeek.ordinal // 0=Monday

                // Lógica de visualización simple en filas
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
                onClick = { onSave(name, type, selectedDate) },
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

@Composable
fun AlertCard(title: String, subtitle: String, isUrgent: Boolean) {
    HuertaCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if(isUrgent) Icons.Filled.WaterDrop else Icons.Filled.Notifications,
                null,
                tint = if(isUrgent) RedDanger else GreenPrimary
            )
            Column(Modifier.padding(start = 16.dp)) {
                Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}