package com.example.proyecto.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavController
import com.example.proyecto.ui.navigation.AppScreens
import com.example.proyecto.ui.theme.GreenPrimary
import kotlinx.datetime.*
import org.jetbrains.compose.resources.stringResource
import huertomanager.composeapp.generated.resources.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.foundation.horizontalScroll
import com.example.proyecto.ui.garden.GardenViewModel
import org.koin.compose.viewmodel.koinViewModel

// GESTOR DE ACCESOS DIRECTOS (El "array" global)
object ShortcutManager {
    val pinnedGardenIds = mutableStateListOf<Long>()
}

@Composable
fun HomeScreen(navController: NavController, viewModel: GardenViewModel = koinViewModel()) {
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    val now = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }

    // Obtenemos las jardineras reales del ViewModel
    val jardineras by viewModel.jardineras.collectAsState()

    // Filtramos las jardineras que están en nuestro array de IDs anclados
    val pinnedGardens = jardineras.filter { ShortcutManager.pinnedGardenIds.contains(it.id) }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("${today.dayOfMonth}/${today.monthNumber}", color = MaterialTheme.colorScheme.secondary)
                Text("${stringResource(Res.string.home_greeting)}\n${stringResource(Res.string.home_role)}", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(
                onClick = { navController.navigate(AppScreens.Alerts) { launchSingleTop = true } },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            ) { Icon(Icons.Filled.Notifications, null, tint = GreenPrimary) }
        }

        WeatherCard(now)

        Spacer(Modifier.height(30.dp))
        Text(stringResource(Res.string.quick_access_title), fontWeight = FontWeight.Bold, fontSize = 18.sp)

        // SECCIÓN DINÁMICA DE ACCESOS RÁPIDOS
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp).horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (pinnedGardens.isEmpty()) {
                // Mensaje si no hay nada anclado
                Text(
                    text = "No tienes accesos directos.\nAncla una jardinera con la chincheta.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            } else {
                pinnedGardens.forEach { garden ->
                    // Buscamos su índice original para que la navegación funcione bien
                    val originalIndex = jardineras.indexOf(garden)

                    Card(
                        modifier = Modifier.width(120.dp).height(80.dp).clickable {
                            navController.navigate("garden/$originalIndex") { launchSingleTop = true }
                        },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Grass, null, tint = GreenPrimary)
                            Text(garden.nombre, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                }
            }
        }

        Box(Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { /* Navegar a Chat IA */ }, Modifier.size(110.dp)) {
                    Icon(Icons.Default.AutoAwesome, null, tint = GreenPrimary, modifier = Modifier.fillMaxSize())
                }
                Text(stringResource(Res.string.ai_chat_helper), fontSize = 12.sp, color = GreenPrimary, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(100.dp))
    }
}

@Composable
fun WeatherCard(time: LocalDateTime) {
    val isNight = time.hour > 20 || time.hour < 7
    val bgColor = if (isNight) Color(0xFF2C3E50) else Color(0xFFF1C40F).copy(alpha = 0.15f)
    Card(Modifier.fillMaxWidth().height(140.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = bgColor)) {
        Row(Modifier.fillMaxSize().padding(24.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(stringResource(Res.string.weather_title_now), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(if(isNight) "18°C" else "26°C", fontSize = 42.sp, fontWeight = FontWeight.Bold)
                Text(if(isNight) stringResource(Res.string.weather_night) else stringResource(Res.string.weather_sunny))
            }
            Icon(if(isNight) Icons.Default.NightsStay else Icons.Default.WbSunny, null, Modifier.size(70.dp), tint = if(isNight) Color(0xFFF1C40F) else Color(0xFFE67E22))
        }
    }
}