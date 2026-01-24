package com.example.proyecto.ui.garden

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto.di.AppModule
import com.example.proyecto.ui.navigation.AppScreens

@Composable
fun GardenScreen(
    navController: NavController,
    viewModel: GardenViewModel = viewModel { GardenViewModel(AppModule.huertaRepository) }
) {
    val jardineras by viewModel.jardinerasUi.collectAsState()

    // Estados para el Diálogo de Crear
    var showCreateDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newRows by remember { mutableStateOf("2") }
    var newCols by remember { mutableStateOf("4") }

    // Estados para Borrar (CORREGIDO: Tipo explícito <JardineraUi?>)
    var showDeleteDialog by remember { mutableStateOf(false) }
    var jardineraToDelete by remember { mutableStateOf<JardineraUi?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Jardinera")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            Text(
                "Mis Jardineras",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(20.dp))

            if (jardineras.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay jardineras. ¡Crea una!", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(jardineras) { item ->
                        JardineraUiCard(
                            jardinera = item,
                            onClick = { navController.navigate(AppScreens.createSlotDetailRoute(item.id)) },
                            onLongClick = {
                                jardineraToDelete = item
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // --- DIÁLOGO CREAR ---
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Nueva Jardinera") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Nombre") }
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newRows, onValueChange = { newRows = it },
                            label = { Text("Filas") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = newCols, onValueChange = { newCols = it },
                            label = { Text("Cols") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val f = newRows.toIntOrNull() ?: 2
                    val c = newCols.toIntOrNull() ?: 4
                    viewModel.crearJardinera(newName.ifBlank { "Sin nombre" }, f, c)
                    showCreateDialog = false
                    newName = ""; newRows = "2"; newCols = "4"
                }) { Text("Crear") }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // --- DIÁLOGO BORRAR ---
    if (showDeleteDialog && jardineraToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar Jardinera?") },
            text = { Text("Se borrarán todos los cultivos de '${jardineraToDelete?.nombre}'.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.borrarJardinera(jardineraToDelete!!.id)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun JardineraUiCard(jardinera: JardineraUi, onClick: () -> Unit, onLongClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                jardinera.icon,
                null,
                tint = jardinera.color,
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(
                    jardinera.nombre,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    jardinera.descripcion,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}