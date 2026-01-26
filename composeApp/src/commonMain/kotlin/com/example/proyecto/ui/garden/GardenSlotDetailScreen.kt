package com.example.proyecto.ui.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto.di.AppModule
import com.example.proyecto.ui.HuertaCard
import com.example.proyecto.ui.theme.GreenPrimary
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenSlotDetailScreen(
    navController: NavController,
    slotName: String,
    viewModel: BancalDetailViewModel = viewModel { BancalDetailViewModel(AppModule.huertaRepository, slotName) }
) {
    val bancal by viewModel.bancal.collectAsState()
    val historial by viewModel.historial.collectAsState()

    if (bancal == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    val currentBancal = bancal!!
    val planta = currentBancal.planta

    // INFO DE RIEGO CALCULADA
    val infoRiego = viewModel.getInfoRiego(currentBancal)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Cultivo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 1. FICHA PLANTA
            HuertaCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Eco, null, modifier = Modifier.size(64.dp), tint = GreenPrimary)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(text = planta?.nombre ?: "Sin Planta", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        if (planta != null) {
                            Text(text = "Variedad: ${planta.variedad}", color = Color.Gray)
                            Surface(color = GreenPrimary.copy(alpha = 0.2f), shape = RoundedCornerShape(50)) {
                                Text(text = "EN CRECIMIENTO", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = GreenPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // 2. NUEVO: TARJETA DE RIEGO
            if (planta != null) {
                HuertaCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Próximo Riego Estimado", fontSize = 12.sp, color = Color.Gray)
                            Text(infoRiego.first, fontWeight = FontWeight.Bold, color = infoRiego.second, fontSize = 16.sp)
                        }
                        Button(
                            onClick = { viewModel.regar() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)) // Azul Riego
                        ) {
                            Icon(Icons.Filled.WaterDrop, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Regar")
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // 3. HISTORIAL
            Text("Historial de este bancal", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))

            if (historial.isEmpty()) {
                Text("No hay eventos registrados.", color = Color.Gray, fontSize = 14.sp)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    items(historial) { evento ->
                        HistorialItem(evento)
                    }
                }
            }
        }
    }
}

@Composable
fun HistorialItem(evento: com.example.proyecto.domain.model.EntradaDiario) {
    val fecha = Instant.fromEpochMilliseconds(evento.fecha).toLocalDateTime(TimeZone.currentSystemDefault()).date
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(if (evento.tipo.name == "RIEGO") Icons.Filled.WaterDrop else Icons.Filled.History, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(evento.titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("${fecha.dayOfMonth}/${fecha.monthNumber}", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}