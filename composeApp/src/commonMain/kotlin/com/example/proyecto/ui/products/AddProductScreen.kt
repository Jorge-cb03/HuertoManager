package com.example.proyecto.ui.products

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.proyecto.domain.model.ProductType
import com.example.proyecto.ui.HuertaInput
import com.example.proyecto.ui.garden.GardenViewModel
import com.example.proyecto.ui.theme.GreenPrimary
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    navController: NavController,
    productId: String? = null,
    viewModel: GardenViewModel = koinViewModel()
) {
    val idLong = productId?.toLongOrNull() ?: 0L
    val isEditMode = productId != null

    // Estados
    var name by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var perenualId by remember { mutableStateOf<Int?>(null) }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    var scientificName by remember { mutableStateOf<String?>(null) }
    var selectedType by remember { mutableStateOf(ProductType.SEED) }

    // Estados UI
    var expandedType by remember { mutableStateOf(false) }
    var showApiSearchDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val apiResults by viewModel.apiSearchResults.collectAsState()

    // Cargar datos
    LaunchedEffect(productId) {
        if (isEditMode) {
            val p = viewModel.getProductoById(idLong)
            p?.let {
                name = it.nombre
                stock = it.stock.toString()
                notes = it.notasCultivo ?: ""
                perenualId = it.perenualId
                selectedImageUrl = it.imagenUrl
                scientificName = it.nombreCientifico
                selectedType = try { ProductType.valueOf(it.categoria) } catch(e: Exception) { ProductType.SEED }
            }
        }
    }

    val isSeed = selectedType == ProductType.SEED
    val unitLabel = if (selectedType == ProductType.FERTILIZER || selectedType == ProductType.CHEMICAL) " (kg/L)" else " (unds)"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Editar Producto" else "Añadir Producto") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Ficha Técnica", style = MaterialTheme.typography.titleMedium, color = GreenPrimary)

                    if (!selectedImageUrl.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = selectedImageUrl,
                                contentDescription = null,
                                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(8.dp)).border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Vinculado con Catálogo", color = GreenPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                if (perenualId != null) Text("ID: $perenualId", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }

                    Box {
                        OutlinedTextField(
                            value = selectedType.name,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoría") },
                            trailingIcon = { IconButton(onClick = { expandedType = true }) { Icon(Icons.Default.ArrowDropDown, null) } },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(expanded = expandedType, onDismissRequest = { expandedType = false }) {
                            ProductType.entries.forEach { type ->
                                DropdownMenuItem(text = { Text(type.name) }, onClick = { selectedType = type; expandedType = false })
                            }
                        }
                    }

                    if (isSeed) {
                        Button(
                            onClick = {
                                showApiSearchDialog = true
                                searchQuery = ""
                                // CAMBIO: Cargar TODO el catálogo al abrir
                                viewModel.buscarCultivoApi("")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                        ) {
                            Icon(Icons.Default.Search, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Seleccionar de Base de Datos")
                        }
                    }

                    HuertaInput(value = name, onValueChange = { name = it }, label = "Nombre Común", icon = Icons.Default.Edit)
                    HuertaInput(value = stock, onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) stock = it }, label = "Stock $unitLabel", icon = Icons.Default.Inventory)

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notas, Asociaciones, Fechas...") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        maxLines = 4
                    )
                }
            }

            Button(
                onClick = {
                    viewModel.guardarProducto(idLong, name, selectedType.name, stock.toDoubleOrNull() ?: 0.0, perenualId, selectedImageUrl, scientificName, notes)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) { Text("Guardar Ficha") }
        }
    }

    if (showApiSearchDialog) {
        AlertDialog(
            onDismissRequest = { showApiSearchDialog = false },
            title = { Text("Catálogo de Cultivos") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Filtrar (ej: Tomate)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, null) }
                    )

                    // CAMBIO: Búsqueda reactiva inmediata (predictiva)
                    LaunchedEffect(searchQuery) {
                        viewModel.buscarCultivoApi(searchQuery)
                    }

                    Spacer(Modifier.height(16.dp))

                    Box(Modifier.fillMaxWidth().height(300.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(0.3f))) {
                        LazyColumn(Modifier.fillMaxSize()) {
                            items(apiResults) { crop ->
                                ListItem(
                                    headlineContent = { Text(crop.commonName) },
                                    leadingContent = {
                                        val img = crop.defaultImage?.regularUrl
                                        AsyncImage(
                                            model = img,
                                            contentDescription = null,
                                            modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.White),
                                            contentScale = ContentScale.Crop,
                                            placeholder = rememberVectorPainter(Icons.Default.Eco),
                                            error = rememberVectorPainter(Icons.Default.Eco)
                                        )
                                    },
                                    modifier = Modifier.clickable {
                                        name = crop.commonName
                                        perenualId = crop.id
                                        selectedImageUrl = crop.defaultImage?.regularUrl
                                        scientificName = crop.scientificName.firstOrNull()
                                        showApiSearchDialog = false
                                    }
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showApiSearchDialog = false }) { Text("Cancelar") } }
        )
    }
}