package com.example.proyecto.ui.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import com.example.proyecto.ui.HuertaCard
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.LocalDate
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlinx.datetime.minus
import kotlinx.datetime.daysUntil

fun getDaysInMonth(month: Int, year: Int): Int {
    val start = LocalDate(year, month, 1)
    val nextMonth = start.plus(1, DateTimeUnit.MONTH)
    return start.daysUntil(nextMonth)
}

fun getFirstDayOfWeek(month: Int, year: Int): Int {
    return LocalDate(year, month, 1).dayOfWeek.ordinal
}

@Composable
fun getMonthNameResource(monthNumber: Int): String {
    return when(monthNumber) {
        1 -> stringResource(Res.string.month_1); 2 -> stringResource(Res.string.month_2)
        3 -> stringResource(Res.string.month_3); 4 -> stringResource(Res.string.month_4)
        5 -> stringResource(Res.string.month_5); 6 -> stringResource(Res.string.month_6)
        7 -> stringResource(Res.string.month_7); 8 -> stringResource(Res.string.month_8)
        9 -> stringResource(Res.string.month_9); 10 -> stringResource(Res.string.month_10)
        11 -> stringResource(Res.string.month_11); 12 -> stringResource(Res.string.month_12)
        else -> ""
    }
}

@Composable
fun DiaryScreen() {
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }

    var currentYear by remember { mutableStateOf(today.year) }
    var currentMonth by remember { mutableStateOf(today.monthNumber) }
    var selectedDate by remember { mutableStateOf(today) }

    val daysInMonth = getDaysInMonth(currentMonth, currentYear)
    val firstDayOfWeek = getFirstDayOfWeek(currentMonth, currentYear)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) { Icon(Icons.Filled.Edit, null) }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)) {

            // HEADER DEL MES
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    val newDate = LocalDate(currentYear, currentMonth, 1).minus(1, DateTimeUnit.MONTH)
                    currentMonth = newDate.monthNumber; currentYear = newDate.year
                    selectedDate = LocalDate(currentYear, currentMonth, 1)
                }) { Icon(Icons.Filled.ChevronLeft, null, tint = MaterialTheme.colorScheme.onBackground) }

                Text("${getMonthNameResource(currentMonth)} $currentYear", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)

                IconButton(onClick = {
                    val newDate = LocalDate(currentYear, currentMonth, 1).plus(1, DateTimeUnit.MONTH)
                    currentMonth = newDate.monthNumber; currentYear = newDate.year
                    selectedDate = LocalDate(currentYear, currentMonth, 1)
                }) { Icon(Icons.Filled.ChevronRight, null, tint = MaterialTheme.colorScheme.onBackground) }
            }

            // DÍAS SEMANA
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("L", "M", "X", "J", "V", "S", "D").forEach { day ->
                    Text(day, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(10.dp))

            // GRID CALENDARIO
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
                    val hasEvent = (dayNum % 5 == 0)

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.aspectRatio(1f).clip(CircleShape)
                            .background(when { isSelected -> MaterialTheme.colorScheme.primary; isToday -> MaterialTheme.colorScheme.surfaceVariant; else -> Color.Transparent })
                            .clickable { selectedDate = dateOfCell }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$dayNum", color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground, fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal)
                            if (hasEvent && !isSelected) {
                                Box(modifier = Modifier.size(4.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            // TEXTOS TRADUCIDOS
            Text("${stringResource(Res.string.tasks_for)} ${selectedDate.dayOfMonth} ${stringResource(Res.string.of)} ${getMonthNameResource(selectedDate.monthNumber)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(10.dp))

            // LISTA DE TAREAS TRADUCIDA
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (selectedDate == today) {
                    item { DiaryEntryCard(stringResource(Res.string.diary_irrigation_title), "08:00 AM", stringResource(Res.string.diary_irrigation_desc)) }
                } else if (selectedDate.dayOfMonth % 5 == 0) {
                    item { DiaryEntryCard(stringResource(Res.string.diary_review_title), "10:00 AM", stringResource(Res.string.diary_review_desc)) }
                } else {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                            Text(stringResource(Res.string.no_entries), color = MaterialTheme.colorScheme.secondary)
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
            Text(time, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(5.dp))
        Text(content, color = MaterialTheme.colorScheme.secondary, fontSize = 14.sp)
    }
}