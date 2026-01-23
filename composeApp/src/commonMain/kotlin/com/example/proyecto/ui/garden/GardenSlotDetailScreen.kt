package com.example.proyecto.ui.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto.data.local.DiarioEntity
import com.example.proyecto.di.AppModule
import com.example.proyecto.ui.HuertaCard

// Colores auxiliares
val GreenPrimary = Color(0xFF5F9F70)
val GreenSecondary = Color(0xFF4DB6AC)

@Composable
fun GardenSlotDetailScreen(
    navController: NavController,
    slotName: String, // OJO: Aquí 'slotName' es en realidad el ID (uuid)
    // Inyectamos el VM usando la factoría manual
    viewModel: GardenDetailViewModel = viewModel {
        GardenDetailViewModel(AppModule.huertaRepository, slotName)
    }
) {
    // 1. Observamos los datos reales (Reactive UI)
    val jardinera by viewModel.jardinera.collectAsState()
    val diario by viewModel.diario.collectAsState()

    // Si aún no carga la jardinera, mostramos loading (o pantalla vacía)
    if (jardinera == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- CABECERA DINÁMICA ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color.DarkGray)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(top = 40.dp, start = 10.dp)
            ) { Icon(Icons.Filled.ArrowBack, null, tint = Color.White) }

            Column(Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = CircleShape
                ) {
                    Text(
                        text = "Estado: ${jardinera?.estado}", // Dato Real
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
                Spacer(Modifier.height(5.dp))
                Text(
                    text = jardinera?.nombre ?: "Sin nombre", // Dato Real
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // --- LISTA DE DIARIO (LazyColumn) ---
        // Usamos LazyColumn para que sea una lista real scrolleable
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp)
        ) {
            item {
                // Estadísticas (Podrías calcularlas reales más tarde)
                Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                    StatBox("CULTIVO", jardinera?.cultivo ?: "-", Modifier.weight(1f))
                    StatBox("ENTRADAS", "${diario.size}", Modifier.weight(1f), isGreen = true)
                }
                Spacer(Modifier.height(30.dp))
                Text(
                    text = "Historial del Diario",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(15.dp))
            }

            // Aquí pintamos la lista de Firebase/Room
            if (diario.isEmpty()) {
                item { Text("No hay entradas en el diario aún.", color = Color.Gray) }
            } else {
                items(diario) { entrada ->
                    TimelineItem(
                        // Lógica simple para elegir icono según tipo
                        icon = if (entrada.tipo == "Riego") Icons.Filled.WaterDrop else Icons.Filled.Agriculture,
                        color = if (entrada.tipo == "Riego") GreenSecondary else GreenPrimary,
                        title = entrada.titulo,
                        time = "Fecha: ${entrada.fecha}", // Podrías formatear la fecha aquí
                        desc = entrada.descripcion,
                        showLine = diario.last() != entrada
                    )
                }
            }
        }
    }
}

// --- Componentes Auxiliares (Sin cambios) ---
@Composable
fun StatBox(label: String, value: String, modifier: Modifier, isGreen: Boolean = false) {
    HuertaCard(modifier = modifier) {
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if(isGreen) GreenSecondary else MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun TimelineItem(icon: ImageVector, color: Color, title: String, time: String, desc: String, showLine: Boolean) {
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