package com.example.proyecto.ui.garden

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.proyecto.domain.model.Jardinera

@Composable
fun JardineraCard(
    jardinera: Jardinera,
    onClick: () -> Unit
) {
    // 1. LÓGICA DE ESTADO (Calculada al vuelo)
    // Contamos cuántas plantas hay en los bancales
    val huecosOcupados = jardinera.bancales.count { it.planta != null }
    val totalHuecos = jardinera.filas * jardinera.columnas
    val estaVacia = huecosOcupados == 0

    // 2. COLORES DINÁMICOS
    val cardColor = if (estaVacia) {
        MaterialTheme.colorScheme.surfaceVariant // Gris si está vacía
    } else {
        MaterialTheme.colorScheme.primaryContainer // Verde/Color primario si tiene vida
    }

    val contentColor = if (estaVacia) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    Card(
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(1f) // Mantiene la forma cuadrada
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
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // Distribuye espacio
        ) {
            // Icono decorativo (Diferente si está vacía o llena)
            Icon(
                imageVector = if (estaVacia) Icons.Default.Grass else Icons.Default.Eco,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = contentColor.copy(alpha = 0.8f)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Título: Nombre de la Jardinera
                Text(
                    text = jardinera.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Subtítulo: Resumen de ocupación (Ej: "3/8 Plantas")
                Text(
                    text = if (estaVacia) "Disponible" else "$huecosOcupados/$totalHuecos Cultivos",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = contentColor.copy(alpha = 0.7f)
                )

                // Dimensión técnica (Ej: "2x4")
                Text(
                    text = "${jardinera.filas}x${jardinera.columnas} Grid",
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.5f)
                )
            }
        }
    }
}