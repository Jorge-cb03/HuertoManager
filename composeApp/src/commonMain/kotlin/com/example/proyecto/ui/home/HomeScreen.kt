package com.example.proyecto.ui.home

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.ui.HuertaCard
import com.example.proyecto.ui.HuertaInput
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.GreenSecondary
import com.example.proyecto.ui.theme.RedDanger
import kotlinx.datetime.*
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

@Composable
fun HomeScreen(
    navController: NavController,
    onNavigateToDetail: (String) -> Unit = {} // Ya no es crítico aquí, pero lo dejamos por compatibilidad
) {
    // UI Estática de Dashboard (Sin ViewModel por ahora)
    var showAlertDialog by remember { mutableStateOf(false) }
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // ... (Aquí va todo el código original de cabecera y tarjetas de salud que tenías) ...
        // Te resumo la estructura para que copies/pegues dentro:

        // 1. HEADER
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("${today.dayOfMonth}/${today.monthNumber}", color = MaterialTheme.colorScheme.secondary)
                Text(
                    "Hola,\nIsmael", // O string resource
                    fontSize = 32.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground
                )
            }
            IconButton(onClick = { showAlertDialog = true }) {
                Icon(Icons.Filled.Notifications, null, tint = GreenPrimary)
            }
        }

        // 2. TARJETA SALUD
        HuertaCard(modifier = Modifier.height(200.dp)) {
            // ... (Contenido de la tarjeta de salud igual que antes) ...
            // Si necesitas que te pegue el código de la tarjeta dímelo, pero es el mismo de tu diseño original.
            Text("Estado General", color = GreenSecondary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Text("Excelente", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            // ...
        }

        Spacer(Modifier.height(20.dp))

        // 3. ALERTAS
        Text("Alertas Pendientes", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        AlertCard("Riego necesario", "Jardinera A - Tomates", true)
        Spacer(Modifier.height(80.dp))
    }

    // ... (Mantén el AlertSchedulerDialog y AlertCard al final del archivo) ...
}

// Asegúrate de tener estas funciones auxiliares al final del archivo si no las importas de otro lado:
@Composable
fun AlertCard(title: String, subtitle: String, isUrgent: Boolean) {
    HuertaCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if(isUrgent) Icons.Filled.WaterDrop else Icons.Filled.Notifications,
                null,
                tint = if(isUrgent) RedDanger else GreenPrimary
            )
            Column(Modifier.padding(start = 16.dp)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}