package com.example.proyecto.ui.alerts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.ui.HuertaCard
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.RedDanger
import kotlinx.datetime.*
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

// Modelo simple para la lista visual
data class AlertItem(
    val id: Long,
    val title: String,
    val date: LocalDate,
    val daysRemaining: Int
)

@Composable
fun AlertsScreen(navController: NavController) {
    // Datos simulados (En una app real vendrían de base de datos)
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val alertsList = remember { mutableStateListOf(
        AlertItem(1, "Riego Tomates", today, 0),
        AlertItem(2, "Fertilizante", today.plus(2, DateTimeUnit.DAY), 2),
        AlertItem(3, "Cosecha Lechugas", today.plus(5, DateTimeUnit.DAY), 5)
    )}

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Cabecera simple
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.onBackground)
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    stringResource(Res.string.home_alerts_title), // "Alertas" o "Mis Avisos"
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            if (alertsList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(Res.string.no_entries), color = MaterialTheme.colorScheme.secondary)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(alertsList) { alert ->
                        AlertCardItem(
                            alert = alert,
                            onDelete = { alertsList.remove(alert) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlertCardItem(alert: AlertItem, onDelete: () -> Unit) {
    HuertaCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icono
                Surface(
                    shape = CircleShape,
                    color = GreenPrimary.copy(alpha = 0.2f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Notifications, null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(Modifier.width(16.dp))

                Column {
                    Text(
                        text = alert.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // Texto: "24/10/2023 • En 2 días"
                    val dateString = "${alert.date.dayOfMonth}/${alert.date.monthNumber}"
                    val daysText = if (alert.daysRemaining == 0) "HOY" else "En ${alert.daysRemaining} días"

                    Text(
                        text = "$dateString • $daysText",
                        fontSize = 12.sp,
                        color = if(alert.daysRemaining == 0) RedDanger else MaterialTheme.colorScheme.secondary
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
            }
        }
    }
}