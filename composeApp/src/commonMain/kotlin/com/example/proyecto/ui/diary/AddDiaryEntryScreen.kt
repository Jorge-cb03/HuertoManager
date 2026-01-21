package com.example.proyecto.ui.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Title
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
fun AddDiaryEntryScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Estado para el selector de Jardinera
    val gardens = listOf("Invernadero", "Terraza", "Cama Alta", "Macetas") // Ejemplo
    var selectedGarden by remember { mutableStateOf(gardens[0]) }
    var expandedGarden by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.add_diary_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título
            HuertaInput(title, { title = it }, stringResource(Res.string.add_diary_task), Icons.Filled.Title)

            // Selector de Jardinera
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedGarden,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(Res.string.diary_select_garden)) },
                    leadingIcon = { Icon(Icons.Filled.Grass, null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGarden) },
                    modifier = Modifier.fillMaxWidth().clickable { expandedGarden = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Box(Modifier.matchParentSize().clickable { expandedGarden = true })

                DropdownMenu(
                    expanded = expandedGarden,
                    onDismissRequest = { expandedGarden = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    gardens.forEach { garden ->
                        DropdownMenuItem(
                            text = { Text(garden) },
                            onClick = {
                                selectedGarden = garden
                                expandedGarden = false
                            }
                        )
                    }
                }
            }

            // Descripción
            HuertaInput(description, { description = it }, stringResource(Res.string.add_diary_desc), Icons.Filled.Description)

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text(stringResource(Res.string.add_diary_btn))
            }
        }
    }
}