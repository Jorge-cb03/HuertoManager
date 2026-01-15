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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.ui.HuertaCard
import com.example.proyecto.ui.StatusPill
import com.example.proyecto.ui.navigation.AppScreens
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

// Enumeración de tipos
enum class HuertaItemType { PLANT, TOOL, SEED, CHEMICAL }

// Clase de datos
data class HuertaItem(
    val id: String,
    val name: String,
    val type: HuertaItemType,
    val quantityOrDays: String,
    val status: String?,
    val code: String?
)

@Composable
fun ProductsScreen(navController: NavController, onAddProduct: () -> Unit) {
    val inventoryList = listOf(
        HuertaItem("1", "Tomates", HuertaItemType.PLANT, "45", "Sano", "A1"),
        HuertaItem("2", "Pala de Mano", HuertaItemType.TOOL, "1", null, null),
        HuertaItem("3", "Semillas Lechuga", HuertaItemType.SEED, "500g", null, null),
        HuertaItem("4", "Lechugas", HuertaItemType.PLANT, "12", "Enfermo", "A2")
    )

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp)
    ) {
        Text(
            text = stringResource(Res.string.products_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(20.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 80.dp)) {
            items(inventoryList) { item ->
                HuertaItemCard(
                    item = item,
                    onClick = {
                        if (item.type == HuertaItemType.PLANT) {
                            navController.navigate(AppScreens.ProductDetail)
                        }
                    }
                )
            }
            item {
                Button(
                    onClick = onAddProduct,
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Filled.Add, null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(Res.string.products_add_btn))
                }
            }
        }
    }
}

@Composable
fun HuertaItemCard(item: HuertaItem, onClick: () -> Unit) {
    val icon = when (item.type) {
        HuertaItemType.PLANT -> Icons.Filled.Eco
        HuertaItemType.TOOL -> Icons.Filled.Build
        HuertaItemType.SEED -> Icons.Filled.Grain
        HuertaItemType.CHEMICAL -> Icons.Filled.Science
    }

    HuertaCard(onClick = onClick) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), modifier = Modifier.size(48.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        if (item.code != null) Text(item.code, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        else Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(item.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)

                    // TEXTOS DINÁMICOS TRADUCIDOS
                    val subText = if (item.type == HuertaItemType.PLANT)
                        "${item.quantityOrDays} ${stringResource(Res.string.product_days_planted)}"
                    else
                        "${stringResource(Res.string.product_stock_label)} ${item.quantityOrDays}"

                    Text(subText, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                }
            }

            if (item.type == HuertaItemType.PLANT && item.status != null) {
                StatusPill(item.status)
            } else {
                Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = androidx.compose.foundation.shape.RoundedCornerShape(50)) {
                    // TRADUCCIÓN DE TIPOS
                    val typeText = when(item.type) {
                        HuertaItemType.TOOL -> stringResource(Res.string.type_tool)
                        HuertaItemType.SEED -> stringResource(Res.string.type_seed)
                        HuertaItemType.CHEMICAL -> stringResource(Res.string.type_chemical)
                        HuertaItemType.PLANT -> stringResource(Res.string.type_plant)
                    }
                    Text(typeText, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}