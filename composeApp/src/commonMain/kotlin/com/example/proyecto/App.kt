package com.example.proyecto

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import com.example.proyecto.di.appModule
import com.example.proyecto.ui.navigation.AppNavigation // Asegúrate de importar esto
import com.example.proyecto.ui.theme.AppTheme
import org.koin.compose.KoinApplication

@Composable
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        val systemDark = isSystemInDarkTheme()
        // Estado para controlar el tema (puedes expandirlo luego si quieres toggle manual)
        val isDarkTheme by remember { mutableStateOf(systemDark) }

        AppTheme(darkTheme = isDarkTheme) {
            // CORREGIDO: Iniciar la navegación real en lugar de la pantalla de debug
            AppNavigation(
                isDarkTheme = isDarkTheme,
                onToggleTheme = { /* Implementar lógica de toggle si se desea */ }
            )
        }
    }
}