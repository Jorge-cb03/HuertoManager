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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto.di.AppModule
import com.example.proyecto.ui.HuertaCard
import com.example.proyecto.ui.navigation.AppScreens
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.RedDanger
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*
import kotlinx.datetime.*

// Modelo visual (DTO para la UI)
data class DiaryTask(
    val id: String,
    val title: String,
    val time: String,
    val description: String,
    val jardineraName: String,
    val date: LocalDate
)

// Funciones auxiliares de fechas
fun getDaysInMonth(month: Int, year: Int): Int {
    val start = LocalDate(year, month, 1)
    val nextMonth = start.plus(1, DateTimeUnit.MONTH)
    return start.daysUntil(nextMonth)
}
fun getFirstDayOfWeek(month: Int, year: Int): Int { return LocalDate(year, month, 1).dayOfWeek.ordinal }

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
fun DiaryScreen(
    navController: NavController,
    // Inyectamos ViewModel
    viewModel: DiaryViewModel = viewModel { DiaryViewModel(AppModule.huertaRepository) }
) {
    // 1. OBSERVAMOS DATOS REALES
    val allTasks by viewModel.tasks.collectAsState()

    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    var currentYear by remember { mutableStateOf(today.year) }
    var currentMonth by remember { mutableStateOf(today.monthNumber) }
    var selectedDate by remember { mutableStateOf(today) }

    val daysInMonth = getDaysInMonth(currentMonth, currentYear)
    val firstDayOfWeek = getFirstDayOfWeek(currentMonth, currentYear)

    // Estados para borrado
    var showDeleteDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<DiaryTask?>(null) }

    // 2. FILTRAMOS LAS TAREAS REALES POR FECHA
    //val tasksForDay = allTasks.filter { it.date == selectedDate }
    // AHORA (Muestra TODO, ideal para probar, descomenta la línea anterior para filtrar por día)
    val tasksForDay = allTasks
    val groupedTasks = tasksForDay.groupBy { it.jardineraName }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val dateMillis = selectedDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                    navController.navigate(AppScreens.createAddDiaryRoute(dateMillis))
                },
                containerColor = GreenPrimary,
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

            // GRID CALENDARIO (Dibuja los días)
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(280.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Días vacíos al principio del mes
                items(firstDayOfWeek) { Spacer(Modifier) }

                // Días del mes
                items(daysInMonth) { index ->
                    val dayNum = index + 1
                    val dateOfCell = LocalDate(currentYear, currentMonth, dayNum)
                    val isSelected = (dateOfCell == selectedDate)
                    val isToday = (dateOfCell == today)

                    // Comprobamos si hay tareas ese día para poner un puntito
                    val hasTasks = allTasks.any { it.date == dateOfCell }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .background(when {
                                isSelected -> GreenPrimary
                                isToday -> MaterialTheme.colorScheme.surfaceVariant
                                else -> Color.Transparent
                            })
                            .clickable { selectedDate = dateOfCell }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "$dayNum",
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onBackground,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            if (hasTasks && !isSelected) {
                                Box(Modifier.size(4.dp).background(GreenPrimary, CircleShape))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("${stringResource(Res.string.tasks_for)} ${selectedDate.dayOfMonth} ${stringResource(Res.string.of)} ${getMonthNameResource(selectedDate.monthNumber)}", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))

            // LISTA DE TAREAS
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
                            DiaryEntryCard(
                                task = task,
                                onEdit = {
                                    val dateMillis = task.date.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                                    navController.navigate(AppScreens.createAddDiaryRoute(dateMillis))
                                },
                                onDelete = {
                                    taskToDelete = task
                                    showDeleteDialog = true
                                }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }

    // --- DIÁLOGO BORRADO DIARIO ---
    if (showDeleteDialog && taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(Res.string.dialog_delete_slot_title)) },
            text = { Text("¿Eliminar la tarea '${taskToDelete?.title}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.borrarTarea(taskToDelete!!.id)
                        showDeleteDialog = false
                        taskToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDanger)
                ) {
                    Text(stringResource(Res.string.dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(Res.string.dialog_cancel))
                }
            }
        )
    }
}

@Composable
fun DiaryEntryCard(task: DiaryTask, onEdit: () -> Unit, onDelete: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    HuertaCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(task.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)

                    Box {
                        IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Filled.MoreVert, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha=0.5f))
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.menu_edit)) },
                                leadingIcon = { Icon(Icons.Filled.Edit, null) },
                                onClick = { showMenu = false; onEdit() }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.menu_delete), color = RedDanger) },
                                leadingIcon = { Icon(Icons.Filled.Delete, null, tint = RedDanger) },
                                onClick = { showMenu = false; onDelete() }
                            )
                        }
                    }
                }
                Text(task.time, fontSize = 12.sp, color = GreenPrimary)
            }
        }
        Spacer(Modifier.height(5.dp))
        Text(task.description, color = MaterialTheme.colorScheme.secondary, fontSize = 14.sp)
    }
}