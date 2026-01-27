package com.example.proyecto.ui.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.RedDanger
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(navController: NavController, productId: String) {
    // Simulamos la obtención del producto basado en el ID
    val product = remember(productId) {
        val inventory = listOf(
            InventoryItem("1", "Pala de Mano", ProductType.TOOL, "1 ud", "Herramienta de acero inoxidable con mango ergonómico ideal para trasplantes."),
            InventoryItem("2", "Semillas Tomate", ProductType.SEED, "2 sobres", "Variedad Cherry de crecimiento rápido. Requiere exposición directa al sol."),
            InventoryItem("3", "Semillas Lechuga", ProductType.SEED, "50g", "Variedad Romana. Ideal para siembra escalonada en climas templados."),
            InventoryItem("4", "Fertilizante NPK", ProductType.CHEMICAL, "0.5 L", "Abono líquido equilibrado para potenciar el crecimiento y la floración."),
            InventoryItem("5", "Tijeras de Podar", ProductType.TOOL, "2 uds", "Cuchillas con recubrimiento antiadherente para cortes limpios.")
        )
        inventory.find { it.id == productId }
    }

    // Lógica para elegir un consejo aleatorio cada vez que se entra en la pantalla
    val randomTip = remember {
        val tipResources = listOf(
            Res.string.tip_1,
            Res.string.tip_2,
            Res.string.tip_3
        )
        tipResources[Random.nextInt(tipResources.size)]
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.menu_products)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        if (product != null) {
            // Detectamos si el stock es bajo para mostrar una alerta visual
            val isLowStock = product.quantity.filter { it.isDigit() }.toIntOrNull()?.let { it <= 2 } ?: false

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // --- 1. CABECERA VISUAL CON DEGRADADO ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(GreenPrimary.copy(alpha = 0.2f), Color.Transparent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when (product.type) {
                        ProductType.TOOL -> Icons.Filled.Build
                        ProductType.SEED -> Icons.Filled.Grain
                        ProductType.CHEMICAL, ProductType.FERTILIZER -> Icons.Filled.Science
                        else -> Icons.Filled.Inventory2
                    }

                    Surface(
                        modifier = Modifier.size(140.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp,
                        shadowElevation = 8.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(70.dp)
                            )
                        }
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    // --- 2. NOMBRE Y TIPO ---
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Surface(
                        color = GreenPrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = product.type.name,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = GreenPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // --- 3. TARJETA DE STOCK CON ALERTA ---
                    DetailInfoCard(
                        title = stringResource(Res.string.product_stock_label),
                        value = product.quantity,
                        icon = Icons.Filled.Inventory,
                        statusColor = if (isLowStock) RedDanger else GreenPrimary,
                        statusText = if (isLowStock) "STOCK BAJO" else "DISPONIBLE"
                    )

                    Spacer(Modifier.height(24.dp))

                    // --- 4. DESCRIPCIÓN ---
                    Text(
                        text = stringResource(Res.string.add_diary_desc),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = product.description ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    // --- 5. CAJA DE CONSEJO DINÁMICO ---
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.TipsAndUpdates, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = stringResource(Res.string.tip_title),
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = stringResource(randomTip),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(40.dp))
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(Res.string.no_entries))
            }
        }
    }
}

@Composable
fun DetailInfoCard(title: String, value: String, icon: ImageVector, statusColor: Color, statusText: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
                Column(Modifier.padding(start = 16.dp)) {
                    Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                    Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
            }

            Surface(
                color = statusColor.copy(alpha = 0.1f),
                shape = CircleShape,
                border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
            ) {
                Text(
                    text = statusText,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}