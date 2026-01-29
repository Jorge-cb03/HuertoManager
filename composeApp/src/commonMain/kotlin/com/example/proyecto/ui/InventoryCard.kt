package com.example.proyecto.ui

import androidx.compose.foundation.layout.*
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
import com.example.proyecto.domain.model.InventoryItem
import com.example.proyecto.domain.model.ProductType
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.RedDanger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryCard(
    item: InventoryItem,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono según tipo
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = GreenPrimary.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val icon = when (item.type) {
                        ProductType.SEED -> Icons.Default.Spa
                        ProductType.TOOL -> Icons.Default.Build
                        ProductType.CHEMICAL -> Icons.Default.Science
                        else -> Icons.Default.Inventory2
                    }
                    Icon(icon, null, tint = GreenPrimary)
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(item.description ?: "", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(item.quantity, fontWeight = FontWeight.ExtraBold, color = GreenPrimary)
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(20.dp), tint = Color.Gray)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(20.dp), tint = RedDanger)
                    }
                }
            }
        }
    }
}