package com.example.proyecto.ui.garden

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyecto.di.AppModule
import com.example.proyecto.ui.navigation.AppScreens

@Composable
fun GardenScreen(
    navController: NavController,
    // Inyectamos aquí el ViewModel específico del Jardín
    viewModel: GardenViewModel = viewModel { GardenViewModel(AppModule.huertaRepository) }
) {
    val jardineras by viewModel.jardineras.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.crearJardineraTest() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Jardinera")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(20.dp))
            Text(
                "Mis Jardineras",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Gestiona tus cultivos",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(Modifier.height(20.dp))

            if (jardineras.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay jardineras activas", color = Color.Gray)
                }
            } else {
                // AQUÍ ESTÁ LA CLAVE: LazyVerticalGrid con datos reales
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // 2 columnas como pide el PDF
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(jardineras) { jardinera ->
                        JardineraCard(
                            jardinera = jardinera,
                            onClick = {
                                // Navegamos al detalle pasando el ID real
                                navController.navigate(AppScreens.createSlotDetailRoute(jardinera.id))
                            }
                        )
                    }
                }
            }
        }
    }
}