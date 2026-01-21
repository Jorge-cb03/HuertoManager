package com.example.proyecto.ui.products

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(navController: NavController, productId: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Producto") },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono grande simulando la foto del producto
            Icon(
                imageVector = Icons.Filled.Eco,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(100.dp)
            )

            Spacer(Modifier.height(20.dp))

            // Información del producto (Simulada basada en el ID)
            Text(
                text = "Producto #$productId",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Aquí irían los detalles completos, historial de uso, y opciones avanzadas para el producto seleccionado.",
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}