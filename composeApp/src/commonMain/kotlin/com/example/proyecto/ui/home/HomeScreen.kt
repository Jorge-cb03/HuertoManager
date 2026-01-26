package com.example.proyecto.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
// Importamos los modelos
import com.example.proyecto.domain.model.EntradaDiario
import com.example.proyecto.ui.HuertaCard
import com.example.proyecto.ui.navigation.AppScreens
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.GreenSecondary
import kotlinx.datetime.*

@Composable
fun HomeScreen(
    navController: NavController? = null,
    viewModel: HomeViewModel = viewModel { HomeViewModel(AppModule.huertaRepository) }
) {
    val stats by viewModel.stats.collectAsState()
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("${today.dayOfMonth}/${today.monthNumber}", color = MaterialTheme.colorScheme.secondary)
                Text(
                    "Resumen del\nHuerto",
                    fontSize = 32.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // BOTÓN CAMPANA -> CREAR ALERTA
            IconButton(
                onClick = {
                    navController?.navigate(AppScreens.createAddDiaryRoute(Clock.System.now().toEpochMilliseconds()))
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)
            ) {
                Icon(Icons.Filled.Notifications, null, tint = GreenPrimary)
            }
        }

        // --- TARJETA SALUD ---
        HuertaCard(modifier = Modifier.height(200.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("ESTADO GENERAL", color = GreenSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text("Salud del Huerto", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(4.dp))
                    Text("${stats.plantasActivas} plantas en ${stats.totalHuecos} huecos", fontSize = 12.sp, color = Color.Gray)
                }
                Box(modifier = Modifier.size(60.dp).border(4.dp, GreenSecondary, CircleShape), contentAlignment = Alignment.Center) {
                    Text("${stats.saludHuerto}%", color = GreenSecondary, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.weight(1f))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                WeatherItem(Icons.Filled.WaterDrop, stats.humedad, "Humedad")
                WeatherItem(Icons.Filled.WbSunny, stats.temperatura, "Temp")
                WeatherItem(Icons.Filled.Air, stats.uv, "UV")
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("Alertas y Notificaciones", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(10.dp))

        if (stats.alertasFuturas.isEmpty()) {
            Text("No tienes alertas pendientes.", color = Color.Gray)
        } else {
            // LISTA DE ALERTAS
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                stats.alertasFuturas.forEach { alerta ->
                    AlertCard(alerta)
                }
            }
        }
        Spacer(Modifier.height(80.dp))
    }
}

@Composable
fun AlertCard(alerta: EntradaDiario) {
    val fecha = Instant.fromEpochMilliseconds(alerta.fecha).toLocalDateTime(TimeZone.currentSystemDefault())

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha=0.1f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha=0.3f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.NotificationsActive, null, tint = MaterialTheme.colorScheme.error)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(alerta.titulo, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    text = "${fecha.dayOfMonth}/${fecha.monthNumber} - ${fecha.hour}:${fecha.minute.toString().padStart(2,'0')}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error
                )
                if(alerta.descripcion.isNotBlank()) {
                    Text(alerta.descripcion, fontSize = 11.sp, color = Color.Gray, maxLines = 1)
                }
            }
        }
    }
}

@Composable
fun WeatherItem(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.secondary)
        Text(value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
    }
}