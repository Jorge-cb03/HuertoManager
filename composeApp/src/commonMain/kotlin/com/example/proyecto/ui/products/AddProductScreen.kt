package com.example.proyecto.ui.products

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto.ui.HuertaInput
import com.example.proyecto.ui.theme.GreenPrimary
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ProductType.TOOL) }
    var expandedType by remember { mutableStateOf(false) }

    // ALERTA DE ÉXITO
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Función auxiliar para traducir el tipo de producto al vuelo
    @Composable
    fun getProductTypeLabel(type: ProductType): String {
        return when (type) {
            ProductType.TOOL -> stringResource(Res.string.type_tool)
            ProductType.SEED -> stringResource(Res.string.type_seed)
            ProductType.CHEMICAL -> stringResource(Res.string.type_chemical)
            ProductType.FERTILIZER -> stringResource(Res.string.type_fertilizer)
            ProductType.OTHER -> stringResource(Res.string.diary_chip_other)
            else -> type.name // Por si añades más tipos sin traducir
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.add_prod_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campos de texto traducidos
            HuertaInput(name, { name = it }, stringResource(Res.string.add_prod_name), Icons.Filled.Label)
            HuertaInput(stock, { stock = it }, stringResource(Res.string.add_prod_stock), Icons.Filled.Inventory)

            // Selector de Tipo de Producto Traducido
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    // Aquí usamos la función de traducción para el valor mostrado
                    value = getProductTypeLabel(selectedType),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(Res.string.add_prod_type)) },
                    leadingIcon = { Icon(Icons.Filled.Category, null) },
                    modifier = Modifier.fillMaxWidth().clickable { expandedType = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                // Capa invisible para detectar el click sobre el campo deshabilitado
                Box(Modifier.matchParentSize().clickable { expandedType = true })

                DropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                    ProductType.entries.forEach { type ->
                        DropdownMenuItem(
                            // Aquí traducimos cada opción de la lista desplegable
                            text = { Text(getProductTypeLabel(type)) },
                            onClick = {
                                selectedType = type
                                expandedType = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { showSuccessDialog = true },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text(stringResource(Res.string.add_prod_btn))
            }
        }
    }

    // Diálogo de éxito traducido
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { /* Bloqueo */ },
            title = { Text(stringResource(Res.string.dialog_success_title)) },
            text = { Text(stringResource(Res.string.dialog_success_product_saved)) },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text(stringResource(Res.string.dialog_btn_ok))
                }
            }
        )
    }
}