package com.example.proyecto.ui.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.ui.HuertaInput
import com.example.proyecto.ui.theme.GreenPrimary
import org.jetbrains.compose.resources.stringResource
import proyecto.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    navController: NavController,
    productId: String? = null,
    initialName: String? = null,
    initialStock: String? = null,
    initialType: String? = null
) {
    val isEditMode = productId != null
    var name by remember { mutableStateOf(initialName ?: "") }
    var stock by remember { mutableStateOf(initialStock ?: "") }
    var description by remember { mutableStateOf("") } // NUEVO CAMPO

    val initialEnum = try { ProductType.valueOf(initialType ?: "TOOL") } catch (e: Exception) { ProductType.TOOL }
    var selectedType by remember { mutableStateOf(initialEnum) }
    var expandedType by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    @Composable
    fun getProductTypeLabel(type: ProductType): String {
        return when (type) {
            ProductType.TOOL -> stringResource(Res.string.type_tool)
            ProductType.SEED -> stringResource(Res.string.type_seed)
            ProductType.CHEMICAL -> stringResource(Res.string.type_chemical)
            ProductType.FERTILIZER -> stringResource(Res.string.type_fertilizer)
            ProductType.OTHER -> stringResource(Res.string.diary_chip_other)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(if (isEditMode) Res.string.menu_edit else Res.string.add_prod_title)) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = stringResource(Res.string.section_basic_info),
                        style = MaterialTheme.typography.titleMedium,
                        color = GreenPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    HuertaInput(name, { name = it }, stringResource(Res.string.add_prod_name), Icons.Default.Label)

                    // ÁREA DE DESCRIPCIÓN MULTILÍNEA
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(stringResource(Res.string.add_prod_desc_hint)) },
                        leadingIcon = { Icon(Icons.Default.Description, null) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GreenPrimary, focusedLabelColor = GreenPrimary)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.weight(1f)) {
                            HuertaInput(stock, { stock = it }, stringResource(Res.string.add_prod_stock), Icons.Default.Inventory)
                        }
                        Box(Modifier.weight(1.2f)) {
                            OutlinedTextField(
                                value = getProductTypeLabel(selectedType),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(Res.string.add_prod_type)) },
                                trailingIcon = { IconButton(onClick = { expandedType = true }) { Icon(Icons.Default.ArrowDropDown, null) } },
                                modifier = Modifier.fillMaxWidth()
                            )
                            DropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                                ProductType.entries.forEach { type ->
                                    DropdownMenuItem(text = { Text(getProductTypeLabel(type)) }, onClick = { selectedType = type; expandedType = false })
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { showSuccessDialog = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(stringResource(if (isEditMode) Res.string.profile_edit_save else Res.string.add_prod_btn), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(Res.string.dialog_success_title)) },
            text = { Text(stringResource(Res.string.dialog_success_product_saved)) },
            confirmButton = { Button(onClick = { showSuccessDialog = false; navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) { Text(stringResource(Res.string.dialog_btn_ok)) } }
        )
    }
}