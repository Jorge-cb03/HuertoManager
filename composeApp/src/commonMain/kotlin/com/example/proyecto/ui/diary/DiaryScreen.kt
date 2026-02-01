package com.example.proyecto.ui.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.ui.HuertaCard
import com.example.proyecto.ui.garden.GardenViewModel
import com.example.proyecto.ui.navigation.AppScreens
import com.example.proyecto.ui.theme.GreenPrimary
import kotlinx.datetime.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DiaryScreen(navController: NavController, viewModel: GardenViewModel = koinViewModel()) {
    val historialState = viewModel.historialGeneral.collectAsState()
    val historial = historialState.value

    // --- ESTADO DEL CALENDARIO ---
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    var selectedDate by remember { mutableStateOf(today) }
    var currentMonth by remember { mutableStateOf(today.monthNumber) }
    var currentYear by remember { mutableStateOf(today.year) }

    // Filtramos las entradas para el día seleccionado
    val entriesForSelectedDay = historial.filter {
        val date = Instant.fromEpochMilliseconds(it.fecha).toLocalDateTime(TimeZone.currentSystemDefault()).date
        date == selectedDate
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text(
            text = "Diario de Campo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = GreenPrimary
        )

        Spacer(Modifier.height(20.dp))

        // --- CALENDARIO ---
        HuertaCard {
            Column(Modifier.padding(8.dp)) {
                // Cabecera del calendario (Mes Año y flechas)
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (currentMonth == 1) { currentMonth = 12; currentYear-- } else currentMonth--
                    }) { Icon(Icons.Default.ChevronLeft, null) }

                    Text(
                        text = "${getMonthName(currentMonth)} $currentYear",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    IconButton(onClick = {
                        if (currentMonth == 12) { currentMonth = 1; currentYear++ } else currentMonth++
                    }) { Icon(Icons.Default.ChevronRight, null) }
                }

                Spacer(Modifier.height(10.dp))

                // Días de la semana
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    listOf("L", "M", "X", "J", "V", "S", "D").forEach { day ->
                        Text(day, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }

                // Cuadrícula de días
                val daysInMonth = getDaysInMonth(currentMonth, currentYear)
                val firstDayOffset = getFirstDayOfWeek(currentMonth, currentYear) // 0=Lunes, 6=Domingo

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.height(240.dp).padding(top = 10.dp)
                ) {
                    // Espacios vacíos al principio del mes
                    items(firstDayOffset) { Spacer(Modifier.fillMaxSize()) }

                    // Días del mes
                    items(daysInMonth) { dayIndex ->
                        val day = dayIndex + 1
                        val isSelected = selectedDate.dayOfMonth == day &&
                                selectedDate.monthNumber == currentMonth &&
                                selectedDate.year == currentYear

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) GreenPrimary else Color.Transparent)
                                .clickable { selectedDate = LocalDate(currentYear, currentMonth, day) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(25.dp))

        // --- LISTA DE TAREAS ---
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Tareas del día",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            TextButton(onClick = {
                val epoch = selectedDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                navController.navigate(AppScreens.createAddDiaryRoute(epoch))
            }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                    Text("Añadir")
                }
            }
        }

        if (entriesForSelectedDay.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No hay tareas registradas este día.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(entriesForSelectedDay) { entrada ->
                    TimelineItem(
                        title = entrada.tipoAccion,
                        desc = entrada.descripcion,
                        time = "Hecho",
                        icon = when(entrada.tipoAccion) {
                            "SIEMBRA" -> Icons.Default.Eco
                            "RIEGO" -> Icons.Default.WaterDrop
                            "PODA" -> Icons.Default.ContentCut
                            "ABONADO" -> Icons.Default.Science
                            "ANTIPLAGA" -> Icons.Default.BugReport
                            "COSECHA" -> Icons.Default.DoneAll
                            else -> Icons.Default.Agriculture
                        },
                        color = GreenPrimary,
                        showLine = false
                    )
                }
            }
        }
    }
}

// --- COMPONENTES VISUALES ---

@Composable
fun TimelineItem(
    title: String,
    desc: String,
    time: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    showLine: Boolean
) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        // Columna del Icono y Línea
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(42.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .border(2.dp, color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            if (showLine) {
                Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(Color.LightGray))
            }
        }

        // Tarjeta de Contenido
        HuertaCard(modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth()) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = title, fontWeight = FontWeight.Bold)
                    Text(text = time, fontSize = 12.sp, color = Color.Gray)
                }
                Text(text = desc, fontSize = 13.sp)
            }
        }
    }
}

// --- UTILIDADES ---

fun getDaysInMonth(month: Int, year: Int): Int {
    val start = LocalDate(year, month, 1)
    val nextMonth = if (month == 12) LocalDate(year + 1, 1, 1) else LocalDate(year, month + 1, 1)
    return start.daysUntil(nextMonth)
}

fun getFirstDayOfWeek(month: Int, year: Int): Int {
    // ordinal: Lunes=0 ... Domingo=6
    return LocalDate(year, month, 1).dayOfWeek.ordinal
}

fun getMonthName(monthNumber: Int): String {
    return when(monthNumber) {
        1 -> "Enero"; 2 -> "Febrero"; 3 -> "Marzo"; 4 -> "Abril"
        5 -> "Mayo"; 6 -> "Junio"; 7 -> "Julio"; 8 -> "Agosto"
        9 -> "Septiembre"; 10 -> "Octubre"; 11 -> "Noviembre"; 12 -> "Diciembre"
        else -> ""
    }
}