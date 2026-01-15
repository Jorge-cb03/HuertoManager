package com.example.proyecto.ui.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.ui.HuertaCard

// Definimos colores específicos para esta pantalla si no están en el tema global
val GreenPrimary = Color(0xFF5F9F70)
val GreenSecondary = Color(0xFF4DB6AC)

@Composable
fun GardenSlotDetailScreen(
    navController: NavController,
    slotName: String // Ej: "A1"
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // --- CABECERA CON IMAGEN ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color.DarkGray) // AQUÍ IRÍA TU FOTO REAL DEL CULTIVO
        ) {
            // Botón Atrás
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(top = 40.dp, start = 10.dp)
            ) {
                Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
            }

            // Información sobre la imagen
            Column(Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = CircleShape
                ) {
                    Text(
                        text = "Posición $slotName - Cama Alta",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "Tomate Cherry", // Esto debería venir de tus datos
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // --- CONTENIDO PRINCIPAL ---
        Column(Modifier.padding(20.dp)) {
            // Estadísticas (Edad / Salud)
            Row(horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                StatBox("EDAD", "45 Días", Modifier.weight(1f))
                StatBox("SALUD", "Buena 🌿", Modifier.weight(1f), isGreen = true)
            }

            Spacer(Modifier.height(30.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Historial del Diario",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = { /* Ir a ver todo el diario */ }) {
                    Text("Ver todo", color = GreenSecondary)
                }
            }
            Spacer(Modifier.height(10.dp))

            // Timeline (Línea de tiempo)
            TimelineItem(
                icon = Icons.Filled.WaterDrop,
                color = GreenSecondary,
                title = "Regado",
                time = "Hoy, 10:00 AM",
                desc = "Riego por goteo activado 20 mins. Humedad óptima.",
                showLine = true
            )
            TimelineItem(
                icon = Icons.Filled.Agriculture,
                color = GreenPrimary,
                title = "Fertilizado",
                time = "Ayer",
                desc = "Aplicación de compost orgánico rico en nitrógeno.",
                showLine = false
            )
        }
    }
}

// --- Componentes Auxiliares (Cajas de Stats y Timeline) ---
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
                modifier = Modifier
                    .size(30.dp)
                    .border(2.dp, color, CircleShape)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
            }
            if (showLine) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                )
            }
        }
        Spacer(Modifier.width(10.dp))
        HuertaCard(modifier = Modifier.padding(bottom = 20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Text(time, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = desc,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp
            )
        }
    }
}