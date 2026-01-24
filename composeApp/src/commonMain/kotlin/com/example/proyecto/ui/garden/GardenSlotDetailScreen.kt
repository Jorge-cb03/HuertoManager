package com.example.proyecto.ui.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.WaterDrop
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
import com.example.proyecto.domain.model.Bancal
import com.example.proyecto.domain.model.EstadoBancal
import com.example.proyecto.domain.model.Producto
import com.example.proyecto.domain.model.TipoEvento
import com.example.proyecto.ui.HuertaCard

// Colores auxiliares
val GreenPrimary = Color(0xFF5F9F70)
val GreenSecondary = Color(0xFF4DB6AC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenSlotDetailScreen(
    navController: NavController,
    slotName: String, // ID de la Jardinera
    viewModel: GardenDetailViewModel = viewModel {
        GardenDetailViewModel(AppModule.huertaRepository, slotName)
    }
) {
    val jardinera by viewModel.jardinera.collectAsState()
    val diario by viewModel.diario.collectAsState()
    val semillas by viewModel.semillasDisponibles.collectAsState()

    // Estados para la interacción con la cuadrícula
    var selectedBancal by remember { mutableStateOf<Bancal?>(null) }
    var showSembrarDialog by remember { mutableStateOf(false) }

    if (jardinera == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    val currentJardinera = jardinera!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- 1. CABECERA ---
        Box(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, null)
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = currentJardinera.nombre,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${currentJardinera.filas}x${currentJardinera.columnas} • ${currentJardinera.bancales.size} huecos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        // --- 2. CUADRÍCULA INTERACTIVA (GRID) ---
        LazyVerticalGrid(
            columns = GridCells.Fixed(currentJardinera.columnas),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val huecos = if (currentJardinera.bancales.isNotEmpty()) {
                currentJardinera.bancales
            } else {
                List(currentJardinera.filas * currentJardinera.columnas) {
                    Bancal(id = "dummy", jardineraId = "", indice = it)
                }
            }

            // CORRECCIÓN CLAVE: 'key' fuerza a Compose a repintar si el ID cambia
            items(
                items = huecos,
                key = { it.id + it.estado.name } // Truco: ID + Estado asegura refresco visual
            ) { bancal ->
                BancalItem(
                    bancal = bancal,
                    onClick = {
                        selectedBancal = bancal
                        showSembrarDialog = true
                    }
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // --- 3. LISTA DE DIARIO ---
        Text(
            text = "Historial Reciente",
            modifier = Modifier.padding(horizontal = 20.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp)
        ) {
            if (diario.isEmpty()) {
                item { Text("No hay entradas aún.", color = Color.Gray) }
            } else {
                items(diario) { entrada ->
                    TimelineItem(
                        icon = if (entrada.tipo == TipoEvento.RIEGO) Icons.Filled.WaterDrop else Icons.Filled.Agriculture,
                        color = if (entrada.tipo == TipoEvento.RIEGO) GreenSecondary else GreenPrimary,
                        title = entrada.titulo,
                        time = "Hace poco", // TODO: Formatear fecha real si se desea
                        desc = entrada.descripcion,
                        showLine = diario.last() != entrada
                    )
                }
            }
        }
    }

    // --- DIÁLOGO DE ACCIONES (Sembrar con Selector / Cosechar) ---
    if (showSembrarDialog && selectedBancal != null) {
        val esVacio = selectedBancal!!.estado == EstadoBancal.VACIO

        // Variables para el selector de semillas
        var semillaSeleccionada by remember { mutableStateOf<Producto?>(null) }
        var menuExpandido by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showSembrarDialog = false },
            title = {
                Text(if (esVacio) "Sembrar Hueco ${selectedBancal!!.indice + 1}" else "Acciones del Cultivo")
            },
            text = {
                if (esVacio) {
                    Column {
                        Text("Elige una semilla de tu inventario:")
                        Spacer(Modifier.height(8.dp))

                        if (semillas.isEmpty()) {
                            Text(
                                "⚠️ No tienes semillas disponibles.",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Ve a la sección Productos y añade 'Semillas' primero.",
                                fontSize = 12.sp
                            )
                        } else {
                            // Selector Desplegable (Dropdown)
                            ExposedDropdownMenuBox(
                                expanded = menuExpandido,
                                onExpandedChange = { menuExpandido = !menuExpandido }
                            ) {
                                OutlinedTextField(
                                    value = semillaSeleccionada?.nombre ?: "Seleccionar semilla...",
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpandido) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = menuExpandido,
                                    onDismissRequest = { menuExpandido = false }
                                ) {
                                    semillas.forEach { semilla ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(semilla.nombre, fontWeight = FontWeight.Bold)
                                                    Text("Stock: ${semilla.cantidad}", fontSize = 10.sp, color = Color.Gray)
                                                }
                                            },
                                            onClick = {
                                                semillaSeleccionada = semilla
                                                menuExpandido = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Column {
                        Text("Planta actual:", fontWeight = FontWeight.Bold)
                        Text(selectedBancal!!.planta?.nombre ?: "Desconocida", fontSize = 18.sp, color = GreenPrimary)
                        Spacer(Modifier.height(8.dp))
                        Text("¿Quieres cosechar o quitar esta planta?")
                    }
                }
            },
            confirmButton = {
                if (esVacio) {
                    Button(
                        onClick = {
                            if (semillaSeleccionada != null) {
                                viewModel.sembrar(selectedBancal!!.id, semillaSeleccionada!!)
                                showSembrarDialog = false
                                semillaSeleccionada = null
                            }
                        },
                        enabled = semillaSeleccionada != null
                    ) { Text("Sembrar") }
                } else {
                    Button(
                        onClick = {
                            viewModel.limpiar(selectedBancal!!.id)
                            showSembrarDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text("Cosechar / Quitar") }
                }
            },
            dismissButton = {
                TextButton(onClick = { showSembrarDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun BancalItem(bancal: Bancal, onClick: () -> Unit) {
    val colorFondo = when (bancal.estado) {
        EstadoBancal.VACIO -> Color(0xFFE0E0E0) // Gris
        EstadoBancal.OCUPADO -> GreenPrimary    // Verde
        EstadoBancal.MUERTO -> Color(0xFF8D6E63) // Marrón
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f) // Cuadrado perfecto
            .clip(RoundedCornerShape(8.dp))
            .background(colorFondo)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (bancal.planta != null) {
            // Mostramos las 2 primeras letras de la planta
            Text(
                text = bancal.planta.nombre.take(2).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        } else {
            // Mostramos el número del hueco
            Text("${bancal.indice + 1}", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun TimelineItem(icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, title: String, time: String, desc: String, showLine: Boolean) {
    Row(Modifier.height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(40.dp)) {
            Box(
                modifier = Modifier.size(30.dp).border(2.dp, color, CircleShape)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = color, modifier = Modifier.size(16.dp)) }
            if (showLine) {
                Box(Modifier.width(2.dp).fillMaxHeight().background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)))
            }
        }
        Spacer(Modifier.width(10.dp))
        HuertaCard(modifier = Modifier.padding(bottom = 20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(time, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
            }
            Spacer(Modifier.height(4.dp))
            Text(text = desc, color = MaterialTheme.colorScheme.secondary, fontSize = 14.sp)
        }
    }
}