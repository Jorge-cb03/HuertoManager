package com.example.proyecto.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
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
import androidx.navigation.NavController
import com.example.proyecto.ui.navigation.AppScreens
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.garden.GardenViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.datetime.*
import org.jetbrains.compose.resources.stringResource
import huertomanager.composeapp.generated.resources.*

object ShortcutManager {
    val pinnedGardenIds = mutableStateListOf<Long>()
}

@Composable
fun HomeScreen(navController: NavController, viewModel: GardenViewModel = koinViewModel()) {
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val now = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }

    val jardineras by viewModel.jardineras.collectAsState()
    val pinnedGardens = jardineras.filter { ShortcutManager.pinnedGardenIds.contains(it.id) }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("${today.dayOfMonth}/${today.monthNumber}", color = MaterialTheme.colorScheme.secondary)
                Text("Hola Jorge,\n¿Cómo va la cosecha?", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = { navController.navigate(AppScreens.Alerts) }) { Icon(Icons.Filled.Notifications, null, tint = GreenPrimary) }
        }

        WeatherCard(now)

        Spacer(Modifier.height(30.dp))
        Text("Accesos Directos", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp).horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (pinnedGardens.isEmpty()) {
                Text("Ancla jardineras con la chincheta para verlas aquí.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(10.dp))
            } else {
                pinnedGardens.forEach { garden ->
                    Card(
                        modifier = Modifier.width(130.dp).height(90.dp).clickable {
                            // NAVEGAMOS POR ID REAL
                            navController.navigate(AppScreens.createGardenRoute(garden.id)) { launchSingleTop = true }
                        },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PushPin, null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
                            Text(garden.nombre, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }
                }
            }
        }

        Box(Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { }, Modifier.size(100.dp)) {
                    Icon(Icons.Default.AutoAwesome, null, tint = GreenPrimary, modifier = Modifier.fillMaxSize())
                }
                Text("Asistente IA", fontSize = 12.sp, color = GreenPrimary, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(100.dp))
    }
}

@Composable
fun WeatherCard(time: LocalDateTime) {
    val isNight = time.hour > 20 || time.hour < 7
    val bgColor = if (isNight) Color(0xFF2C3E50) else Color(0xFFF1C40F).copy(alpha = 0.15f)
    Card(Modifier.fillMaxWidth().height(130.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = bgColor)) {
        Row(Modifier.fillMaxSize().padding(24.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Estado Clima", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(if(isNight) "18°C" else "26°C", fontSize = 42.sp, fontWeight = FontWeight.Bold)
                Text(if(isNight) "Noche Despejada" else "Soleado")
            }
            Icon(if(isNight) Icons.Default.NightsStay else Icons.Default.WbSunny, null, Modifier.size(60.dp), tint = if(isNight) Color(0xFFF1C40F) else Color(0xFFE67E22))
        }
    }
}