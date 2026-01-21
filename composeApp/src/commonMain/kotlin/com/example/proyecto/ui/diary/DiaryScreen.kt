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
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.ui.HuertaCard
import com.example.proyecto.ui.navigation.AppScreens
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.PurpleAccent
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*
import kotlinx.datetime.*

// Modelo de datos interno para el Diario
data class DiaryTask(
    val id: String,
    val title: String,
    val time: String,
    val description: String,
    val jardineraName: String,
    val date: LocalDate
)

fun getDaysInMonth(month: Int, year: Int): Int {
    val start = LocalDate(year, month, 1)
    val nextMonth = start.plus(1, DateTimeUnit.MONTH)
    return start.daysUntil(nextMonth)
}
fun getFirstDayOfWeek(month: Int, year: Int): Int { return LocalDate(year, month, 1).dayOfWeek.ordinal }
@Composable
fun getMonthNameResource(monthNumber: Int): String {
    return when(monthNumber) {
        1 -> stringResource(Res.string.month_1); 2 -> stringResource(Res.string.month_2); 3 -> stringResource(Res.string.month_3); 4 -> stringResource(Res.string.month_4)
        5 -> stringResource(Res.string.month_5); 6 -> stringResource(Res.string.month_6); 7 -> stringResource(Res.string.month_7); 8 -> stringResource(Res.string.month_8)
        9 -> stringResource(Res.string.month_9); 10 -> stringResource(Res.string.month_10); 11 -> stringResource(Res.string.month_11); 12 -> stringResource(Res.string.month_12)
        else -> ""
    }
}

@Composable
fun DiaryScreen(navController: NavController) {
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    var currentYear by remember { mutableStateOf(today.year) }
    var currentMonth by remember { mutableStateOf(today.monthNumber) }
    var selectedDate by remember { mutableStateOf(today) }
    val daysInMonth = getDaysInMonth(currentMonth, currentYear)
    val firstDayOfWeek = getFirstDayOfWeek(currentMonth, currentYear)

    // --- CORRECCIÓN AQUÍ ---
    // 1. Obtenemos los strings fuera del remember
    val irrigationTitle = stringResource(Res.string.diary_irrigation_title)
    val irrigationDesc = stringResource(Res.string.diary_irrigation_desc)
    val reviewTitle = stringResource(Res.string.diary_review_title)
    val reviewDesc = stringResource(Res.string.diary_review_desc)

    // 2. Usamos las variables dentro
    val allTasks = remember(irrigationTitle, irrigationDesc, reviewTitle, reviewDesc, today) {
        listOf(
            DiaryTask("1", irrigationTitle, "08:00 AM", irrigationDesc, "Invernadero", today),
            DiaryTask("2", "Poda Tomates", "08:30 AM", "Quitar chupones", "Invernadero", today),
            DiaryTask("3", "Fertilizar", "10:00 AM", "Abono líquido", "Terraza", today),
            DiaryTask("4", reviewTitle, "10:00 AM", reviewDesc, "Cama Alta", today.plus(5, DateTimeUnit.DAY))
        )
    }

    // Filtramos y agrupamos
    val tasksForDay = allTasks.filter { it.date == selectedDate }
    val groupedTasks = tasksForDay.groupBy { it.jardineraName }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AppScreens.AddDiaryEntry) },
                containerColor = PurpleAccent,
                contentColor = Color.White
            ) { Icon(Icons.Filled.Edit, null) }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
            // HEADER CALENDARIO
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    val newDate = LocalDate(currentYear, currentMonth, 1).minus(1, DateTimeUnit.MONTH)
                    currentMonth = newDate.monthNumber; currentYear = newDate.year
                }) { Icon(Icons.Filled.ChevronLeft, null) }
                Text("${getMonthNameResource(currentMonth)} $currentYear", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = {
                    val newDate = LocalDate(currentYear, currentMonth, 1).plus(1, DateTimeUnit.MONTH)
                    currentMonth = newDate.monthNumber; currentYear = newDate.year
                }) { Icon(Icons.Filled.ChevronRight, null) }
            }

            // GRID
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(280.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(firstDayOfWeek) { Spacer(Modifier) }
                items(daysInMonth) { index ->
                    val dayNum = index + 1
                    val dateOfCell = LocalDate(currentYear, currentMonth, dayNum)
                    val isSelected = (dateOfCell == selectedDate)
                    val isToday = (dateOfCell == today)

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.aspectRatio(1f).clip(CircleShape)
                            .background(when { isSelected -> GreenPrimary; isToday -> MaterialTheme.colorScheme.surfaceVariant; else -> Color.Transparent })
                            .clickable { selectedDate = dateOfCell }
                    ) {
                        Text("$dayNum", color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("${stringResource(Res.string.tasks_for)} ${selectedDate.dayOfMonth} ${stringResource(Res.string.of)} ${getMonthNameResource(selectedDate.monthNumber)}", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))

            // LISTA AGRUPADA POR JARDINERA
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (groupedTasks.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                            Text(stringResource(Res.string.no_entries), color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                } else {
                    groupedTasks.forEach { (jardinera, tasks) ->
                        item {
                            Text(
                                text = jardinera,
                                style = MaterialTheme.typography.titleMedium,
                                color = GreenPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                            )
                        }
                        items(tasks) { task ->
                            DiaryEntryCard(task.title, task.time, task.description)
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiaryEntryCard(title: String, time: String, content: String) {
    HuertaCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Text(time, fontSize = 12.sp, color = GreenPrimary)
        }
        Spacer(Modifier.height(5.dp))
        Text(content, color = MaterialTheme.colorScheme.secondary, fontSize = 14.sp)
    }
}