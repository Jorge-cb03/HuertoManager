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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.data.database.entity.ProductoEntity
import com.example.proyecto.domain.model.ProductType
import com.example.proyecto.ui.garden.GardenViewModel
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.RedDanger
import org.jetbrains.compose.resources.stringResource
import huertomanager.composeapp.generated.resources.*
import org.koin.compose.viewmodel.koinViewModel
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: String,
    viewModel: GardenViewModel = koinViewModel()
) {
    var producto by remember { mutableStateOf<ProductoEntity?>(null) }
    LaunchedEffect(productId) {
        producto = viewModel.getProductoById(productId.toLong())
    }

    val randomTip = remember {
        val tipResources = listOf(Res.string.tip_1, Res.string.tip_2, Res.string.tip_3)
        tipResources[Random.nextInt(tipResources.size)]
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.menu_products)) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        producto?.let { p ->
            val isLowStock = p.stock <= 2
            val pType = try { ProductType.valueOf(p.categoria) } catch(e: Exception) { ProductType.OTHER }

            Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp).background(
                        Brush.verticalGradient(colors = listOf(GreenPrimary.copy(alpha = 0.2f), Color.Transparent))
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(modifier = Modifier.size(140.dp), shape = CircleShape, color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = when(pType) {
                                    ProductType.TOOL -> Icons.Filled.Build
                                    ProductType.SEED -> Icons.Filled.Spa
                                    else -> Icons.Filled.Inventory2
                                },
                                contentDescription = null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(70.dp)
                            )
                        }
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text(text = p.nombre, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)

                    Surface(color = GreenPrimary.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                        Text(text = p.categoria, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = GreenPrimary, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(24.dp))

                    DetailInfoCard(
                        title = stringResource(Res.string.product_stock_label),
                        value = "${p.stock} ${stringResource(Res.string.product_units)}",
                        icon = Icons.Filled.Inventory,
                        statusColor = if (isLowStock) RedDanger else GreenPrimary,
                        statusText = if (isLowStock) stringResource(Res.string.product_stock_low) else stringResource(Res.string.product_available)
                    )

                    Spacer(Modifier.height(24.dp))

                    Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f), shape = RoundedCornerShape(16.dp)) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.TipsAndUpdates, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(text = stringResource(Res.string.tip_title), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(text = stringResource(randomTip), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailInfoCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, statusColor: Color, statusText: String) {
    ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = GreenPrimary.copy(alpha = 0.1f), modifier = Modifier.size(48.dp)) { Box(contentAlignment = androidx.compose.ui.Alignment.Center) { Icon(icon, null, tint = GreenPrimary) } }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) { Text(text = title, style = MaterialTheme.typography.labelMedium, color = Color.Gray); Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
            Surface(color = statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) { Text(text = statusText, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = statusColor, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
        }
    }
}