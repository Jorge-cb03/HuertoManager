package com.example.proyecto.ui.diary

import androidx.compose.foundation.background
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
import com.example.proyecto.data.database.entity.EntradaDiarioEntity
import com.example.proyecto.ui.HuertaCard
import com.example.proyecto.ui.garden.GardenViewModel
import com.example.proyecto.ui.garden.TimelineItem
import com.example.proyecto.ui.navigation.AppScreens
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.RedDanger
import org.jetbrains.compose.resources.stringResource
import huertomanager.composeapp.generated.resources.*
import kotlinx.datetime.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DiaryScreen(navController: NavController, viewModel: GardenViewModel = koinViewModel()) {
    // --- DATOS REALES ---
    val historial by viewModel.historialGeneral.collectAsState()

    // --- ESTADO DEL CALENDARIO (RESTAURADO) ---
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    var selectedDate by remember { mutableStateOf(today) }
    var currentMonth by remember { mutableStateOf(today.monthNumber) }
    var currentYear by remember { mutableStateOf(today.year) }

    // Filtramos las entradas de la DB para el día seleccionado
    val entriesForSelectedDay = historial.filter {
        val date = Instant.fromEpochMilliseconds(it.fecha).toLocalDateTime(TimeZone.currentSystemDefault()).date
        date == selectedDate
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        // Título
        Text(
            text = stringResource(Res.string.diary_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        // --- CALENDARIO (DISEÑO ORIGINAL) ---
        HuertaCard {
            Column {
                // Cabecera mes/año
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (currentMonth == 1) { currentMonth = 12; currentYear-- } else currentMonth--
                    }) { Icon(Icons.Default.ChevronLeft, null) }

                    Text(
                        text = "${getMonthNameResource(currentMonth)} $currentYear",
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
                val firstDay = getFirstDayOfWeek(currentMonth, currentYear)

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.height(220.dp).padding(top = 10.dp)
                ) {
                    // Espacios vacíos
                    items(firstDay) { Spacer(Modifier.fillMaxSize()) }

                    // Días reales
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

        // --- LISTA DE TAREAS (DISEÑO ORIGINAL CON DATOS REALES) ---
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(Res.string.section_tasks),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            TextButton(onClick = { navController.navigate(AppScreens.createAddDiaryRoute(selectedDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds())) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                    Text(stringResource(Res.string.menu_add))
                }
            }
        }

        if (entriesForSelectedDay.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No hay tareas para este día.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(entriesForSelectedDay) { entrada ->
                    // Reutilizamos el TimelineItem para mantener coherencia visual
                    TimelineItem(
                        title = entrada.tipoAccion,
                        desc = entrada.descripcion,
                        time = "Registrado",
                        icon = when(entrada.tipoAccion) {
                            "SIEMBRA" -> Icons.Default.Eco
                            "Riego" -> Icons.Default.WaterDrop
                            "CREACION" -> Icons.Default.AddHome
                            else -> Icons.Default.Agriculture
                        },
                        color = GreenPrimary,
                        showLine = false // En la lista general no hace falta la línea
                    )
                }
            }
        }
    }
}

// --- UTILIDADES (RESTAURADAS) ---

fun getDaysInMonth(month: Int, year: Int): Int {
    val start = LocalDate(year, month, 1)
    val nextMonth = if (month == 12) LocalDate(year + 1, 1, 1) else LocalDate(year, month + 1, 1)
    return start.daysUntil(nextMonth)
}

fun getFirstDayOfWeek(month: Int, year: Int): Int {
    return LocalDate(year, month, 1).dayOfWeek.ordinal
}

@Composable
fun getMonthNameResource(monthNumber: Int): String {
    return when(monthNumber) {
        1 -> "Enero"; 2 -> "Febrero"; 3 -> "Marzo"; 4 -> "Abril"
        5 -> "Mayo"; 6 -> "Junio"; 7 -> "Julio"; 8 -> "Agosto"
        9 -> "Septiembre"; 10 -> "Octubre"; 11 -> "Noviembre"; 12 -> "Diciembre"
        else -> ""
    }
}