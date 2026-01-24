package com.example.proyecto.ui.garden

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.domain.model.EstadoJardinera

@Composable
fun JardineraCard(
    jardinera: Jardinera,
    onClick: () -> Unit
) {
    // Definimos el color según el estado
    val cardColor = when (jardinera.estado) {
        EstadoJardinera.VACIO -> MaterialTheme.colorScheme.surfaceVariant
        EstadoJardinera.OCUPADO -> MaterialTheme.colorScheme.primaryContainer
        EstadoJardinera.ENFERMO -> MaterialTheme.colorScheme.errorContainer
    }

    // Definimos el color del texto secundario para que se lea bien
    val contentColor = when (jardinera.estado) {
        EstadoJardinera.OCUPADO -> MaterialTheme.colorScheme.onPrimaryContainer
        EstadoJardinera.ENFERMO -> MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f) // Cuadrada
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = cardColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. Emoji (Icono)
            Text(
                text = jardinera.icon,
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Título PRINCIPAL: El NOMBRE de la Jardinera (Ej: "Jardinera 1")
            Text(
                text = jardinera.nombre,
                style = MaterialTheme.typography.titleMedium, // Más grande
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // 3. Subtítulo: El CULTIVO (Ej: "Tomates" o "Disponible")
            Text(
                text = jardinera.cultivo ?: "Disponible",
                style = MaterialTheme.typography.bodySmall, // Más pequeño
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = contentColor.copy(alpha = 0.8f) // Un poco más suave
            )
        }
    }
}