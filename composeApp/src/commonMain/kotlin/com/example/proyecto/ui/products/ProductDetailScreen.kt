package com.example.proyecto.ui.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.proyecto.ui.HuertaCard

// Enum solo para tipos de objetos inertes o semillas
enum class ProductType { TOOL, SEED, CHEMICAL, OTHER }

data class InventoryItem(
    val id: String,
    val name: String,
    val type: ProductType,
    val quantity: String, // Ej: "1 ud", "500g", "2L"
    val description: String?
)

@Composable
fun ProductsDetailScreen(
    navController: NavController,
    onAddProduct: () -> Unit
) {
    // Datos simulados: Solo cosas materiales, no plantas vivas
    val inventory = listOf(
        InventoryItem("1", "Pala de Mano", ProductType.TOOL, "1 ud", "Herramienta básica"),
        InventoryItem("2", "Semillas Tomate", ProductType.SEED, "10 sobres", "Variedad Cherry"),
        InventoryItem("3", "Semillas Lechuga", ProductType.SEED, "50g", "Romana"),
        InventoryItem("4", "Fertilizante NPK", ProductType.CHEMICAL, "2.5 L", "Crecimiento"),
        InventoryItem("5", "Tijeras de Podar", ProductType.TOOL, "2 uds", "Acero inoxidable")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Text(
            "Inventario", // Nombre cambiado para reflejar la realidad
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(inventory) { item ->
                InventoryCard(item = item)
            }

            // Botón Añadir Producto al final
            item {
                Button(
                    onClick = onAddProduct,
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Filled.Add, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Añadir al inventario")
                }
            }
        }
    }
}

@Composable
fun InventoryCard(item: InventoryItem) {
    val icon = when (item.type) {
        ProductType.TOOL -> Icons.Filled.Build
        ProductType.SEED -> Icons.Filled.Grain
        ProductType.CHEMICAL -> Icons.Filled.Science
        ProductType.OTHER -> Icons.Filled.Inventory2
    }

    // Colores para etiquetas
    val tagColor = when(item.type) {
        ProductType.TOOL -> Color(0xFF90CAF9) // Azul claro
        ProductType.SEED -> Color(0xFFA5D6A7) // Verde claro
        ProductType.CHEMICAL -> Color(0xFFEF9A9A) // Rojo claro
        else -> Color.Gray
    }

    HuertaCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono en caja
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                modifier = Modifier.size(50.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = item.description ?: "",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Cantidad y Tipo
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = tagColor.copy(alpha = 0.2f),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
                ) {
                    Text(
                        text = item.type.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = tagColor
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = item.quantity,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}