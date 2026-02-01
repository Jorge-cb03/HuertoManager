package com.example.proyecto.ui.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.proyecto.data.database.entity.BancalEntity
import com.example.proyecto.data.database.entity.ProductoEntity
import com.example.proyecto.data.database.entity.EntradaDiarioEntity
import com.example.proyecto.ui.HuertaCard
import com.example.proyecto.ui.theme.GreenPrimary
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenSlotDetailScreen(
    navController: NavController,
    bancalId: String,
    viewModel: GardenViewModel = koinViewModel()
) {
    val id = bancalId.toLongOrNull() ?: 0L
    var activeActionType by remember { mutableStateOf<String?>(null) }

    // ESTADO
    val bancalState = remember { mutableStateOf<BancalEntity?>(null) }
    val bancal = bancalState.value

    val historialState = viewModel.getHistorial(id).collectAsState(initial = emptyList<EntradaDiarioEntity>())
    val historial = historialState.value

    // RECUPERAR DATOS EXTENDIDOS (FICHA)
    val fichaTecnica by remember(bancal) {
        derivedStateOf { viewModel.getInfoExtendida(bancal?.perenualId) }
    }

    LaunchedEffect(id) {
        bancalState.value = viewModel.getBancalById(id)
    }

    val displayImageUrl = bancal?.imagenUrl

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(bancal?.nombreCultivo ?: "Detalle del Bancal") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // FOTO
            Box(modifier = Modifier.fillMaxWidth().height(250.dp).background(MaterialTheme.colorScheme.surfaceVariant)) {
                if (!displayImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = displayImageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = rememberVectorPainter(Icons.Default.Eco),
                        error = rememberVectorPainter(Icons.Default.Eco)
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Eco, null, modifier = Modifier.size(80.dp), tint = GreenPrimary.copy(alpha=0.3f))
                    }
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {

                // 1. INFO BÁSICA
                HuertaCard {
                    Column {
                        Text(bancal?.nombreCultivo ?: "Sin Cultivo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

                        if (bancal?.frecuenciaRiegoDias != null || bancal?.necesidadSol != null) {
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                bancal?.frecuenciaRiegoDias?.let { BadgeInfo(Icons.Default.WaterDrop, "Riego: $it d") }
                                bancal?.necesidadSol?.let { BadgeInfo(Icons.Default.WbSunny, it) }
                            }
                        }
                    }
                }

                // 2. GUÍA DE CULTIVO
                if (fichaTecnica != null) {
                    Spacer(Modifier.height(20.dp))
                    Text("Guía de Cultivo", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(10.dp))

                    HuertaCard {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.ThumbUp, null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("Plantas Amigas:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(fichaTecnica!!.amigos, fontSize = 13.sp, color = Color.Gray)
                                }
                            }
                            HorizontalDivider(color = Color.LightGray.copy(alpha=0.5f))

                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.ThumbDown, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("Evitar plantar cerca:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(fichaTecnica!!.enemigos, fontSize = 13.sp, color = Color.Gray)
                                }
                            }
                            HorizontalDivider(color = Color.LightGray.copy(alpha=0.5f))

                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.Lightbulb, null, tint = Color(0xFFFBC02D), modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("Consejo Pro:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(fichaTecnica!!.consejo, fontSize = 13.sp, fontStyle = FontStyle.Italic)
                                }
                            }
                        }
                    }
                }

                // 3. ACCIONES Y DIARIO
                Spacer(Modifier.height(24.dp))
                Text("Acciones Rápidas", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(10.dp)) {
                    QuickActionItem("Regar", Icons.Default.WaterDrop, Modifier.weight(1f)) { activeActionType = "RIEGO" }

                    // FIX: Reemplazado registrarAccionRapida por guardarEntradaDiario
                    QuickActionItem("Podar", Icons.Default.ContentCut, Modifier.weight(1f)) {
                        viewModel.guardarEntradaDiario(id, "PODA", "Poda realizada", System.currentTimeMillis())
                    }

                    QuickActionItem("Tratar", Icons.Default.BugReport, Modifier.weight(1f)) { activeActionType = "ANTIPLAGA" }
                    QuickActionItem("Abonar", Icons.Default.Science, Modifier.weight(1f)) { activeActionType = "ABONADO" }
                }

                Spacer(Modifier.height(30.dp))
                Text("Historial", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(16.dp))

                if (historial.isEmpty()) {
                    Text("No hay registros.", color = Color.Gray, fontStyle = FontStyle.Italic)
                } else {
                    historial.sortedByDescending { it.fecha }.forEachIndexed { index, entrada ->
                        val date = Instant.fromEpochMilliseconds(entrada.fecha).toLocalDateTime(TimeZone.currentSystemDefault())
                        TimelineItem(
                            t = entrada.tipoAccion,
                            d = entrada.descripcion,
                            tm = "${date.dayOfMonth}/${date.monthNumber}",
                            i = when(entrada.tipoAccion) {
                                "SIEMBRA" -> Icons.Default.Eco
                                "RIEGO" -> Icons.Default.WaterDrop
                                "PODA" -> Icons.Default.ContentCut
                                "COSECHA" -> Icons.Default.ShoppingBasket
                                else -> Icons.Default.History
                            },
                            c = GreenPrimary,
                            s = index != historial.size - 1
                        )
                    }
                }
            }
        }
    }

    if (activeActionType != null) ActionDialog(activeActionType!!, viewModel, id) { activeActionType = null }
}

@Composable
fun BadgeInfo(icon: ImageVector, text: String) {
    Surface(color = GreenPrimary.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(14.dp), tint = GreenPrimary)
            Spacer(Modifier.width(4.dp))
            Text(text, fontSize = 12.sp, color = GreenPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActionDialog(type: String, viewModel: GardenViewModel, bancalId: Long, onDismiss: () -> Unit) {
    var amount by remember { mutableStateOf("") }
    var selectedProduct by remember { mutableStateOf<ProductoEntity?>(null) }
    val productosState = if(type == "ABONADO") {
        viewModel.productosFertilizante.collectAsState(initial = emptyList())
    } else {
        viewModel.productosQuimicos.collectAsState(initial = emptyList())
    }
    val productos = productosState.value

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if(type == "RIEGO") "Registrar Riego" else "Aplicar Tratamiento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (type != "RIEGO") {
                    Text("Producto:", style = MaterialTheme.typography.bodySmall)
                    LazyColumn(Modifier.heightIn(max = 120.dp).fillMaxWidth()) {
                        items(productos) { p ->
                            Row(Modifier.fillMaxWidth().clickable { selectedProduct = p }.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selectedProduct == p, { selectedProduct = p })
                                Text(p.nombre, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = amount,
                    onValueChange = { if(it.all { c -> c.isDigit() || c == '.' }) amount = it },
                    label = { Text(if(type == "RIEGO") "Litros (L)" else "Cantidad") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val value = amount.toDoubleOrNull() ?: 0.0
                    if (type == "RIEGO") viewModel.registrarRiego(bancalId, value)
                    else selectedProduct?.let { viewModel.aplicarTratamiento(bancalId, it, value, type) }
                    onDismiss()
                },
                enabled = amount.isNotEmpty() && (type == "RIEGO" || selectedProduct != null)
            ) { Text("Confirmar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun QuickActionItem(l: String, i: ImageVector, m: Modifier, c: () -> Unit) {
    Card(modifier = m.clickable { c() }, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(12.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(i, null, tint = GreenPrimary)
            Text(l, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun TimelineItem(t: String, d: String, tm: String, i: ImageVector, c: Color, s: Boolean) {
    Row(Modifier.height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(42.dp)) {
            Box(Modifier.size(32.dp).border(2.dp, c, CircleShape), Alignment.Center) {
                Icon(i, null, tint = c, modifier = Modifier.size(16.dp))
            }
            if (s) Box(Modifier.width(2.dp).fillMaxHeight().background(Color.LightGray))
        }
        HuertaCard(Modifier.padding(bottom = 16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                Text(t, fontWeight = FontWeight.Bold)
                Text(tm, fontSize = 12.sp, color = Color.Gray)
            }
            Text(d, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}