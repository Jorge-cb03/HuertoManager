package com.example.proyecto

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.proyecto.ui.navigation.AppNavigation
import com.example.proyecto.ui.theme.AppTheme

@Composable
fun App() {
    // 1. Definimos el estado aquí arriba (puedes usar el del sistema por defecto)
    val systemDark = isSystemInDarkTheme()
    var isDarkTheme by remember { mutableStateOf(systemDark) }

    // 2. Pasamos el estado al Tema
    AppTheme(darkTheme = isDarkTheme) {
        // 3. Pasamos el estado a la navegación para que llegue al Perfil
        AppNavigation(
            isDarkTheme = isDarkTheme,
            onToggleTheme = { isDarkTheme = it }
        )
    }
}