package com.example.proyecto.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Air
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.ui.HuertaCard
import com.example.proyecto.ui.theme.GreenSecondary
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Martes, 24 Oct", color = MaterialTheme.colorScheme.secondary) // Fecha suele ser dinámica, la dejo fija por ahora
                // TEXTOS CAMBIADOS A RECURSOS
                Text("${stringResource(Res.string.home_greeting)}\n${stringResource(Res.string.home_role)}", fontSize = 32.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            }
            IconButton(onClick = {}, modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)) {
                Icon(Icons.Filled.Notifications, null, tint = MaterialTheme.colorScheme.primary)
            }
        }

        // Tarjeta Salud del Huerto
        HuertaCard(modifier = Modifier.height(200.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(stringResource(Res.string.home_status_label), color = GreenSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text(stringResource(Res.string.home_health_title), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                }
                Box(modifier = Modifier.size(60.dp).border(4.dp, GreenSecondary, CircleShape), contentAlignment = Alignment.Center) {
                    Text("85%", color = GreenSecondary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.weight(1f))
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            Spacer(Modifier.weight(1f))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                WeatherItem(Icons.Filled.WaterDrop, "60%", stringResource(Res.string.weather_humidity))
                WeatherItem(Icons.Filled.WbSunny, "24°C", stringResource(Res.string.weather_temp))
                WeatherItem(Icons.Filled.Air, "Alto", stringResource(Res.string.weather_uv))
            }
        }

        Spacer(Modifier.height(20.dp))
        Text(stringResource(Res.string.home_alerts_title), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(10.dp))

        // Alertas
        AlertCard(stringResource(Res.string.alert_irrigation_title), stringResource(Res.string.alert_irrigation_desc), true)
        Spacer(Modifier.height(10.dp))
        AlertCard(stringResource(Res.string.alert_review_title), stringResource(Res.string.alert_review_desc), false)

        Spacer(Modifier.height(80.dp)) // Espacio para bottom bar
    }
}

@Composable
fun WeatherItem(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.secondary)
        Text(value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
fun AlertCard(title: String, subtitle: String, isUrgent: Boolean) {
    HuertaCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if(isUrgent) Icons.Filled.WaterDrop else Icons.Filled.Notifications,
                null,
                tint = if(isUrgent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
            Column(Modifier.padding(start = 16.dp)) {
                Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}