package com.example.proyecto

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import com.example.proyecto.di.appModule
import com.example.proyecto.ui.navigation.AppNavigation
import com.example.proyecto.ui.theme.AppTheme
import org.koin.compose.KoinApplication

@Composable
fun App() {
    // 1. Inicializamos Koin envolviendo toda la estructura
    KoinApplication(application = {
        modules(appModule)
    }) {
        // 2. Definimos el estado del tema
        val systemDark = isSystemInDarkTheme()
        var isDarkTheme by remember { mutableStateOf(systemDark) }

        // 3. Aplicamos el tema y la navegación dentro del contexto de Koin
        AppTheme(darkTheme = isDarkTheme) {
            AppNavigation(
                isDarkTheme = isDarkTheme,
                onToggleTheme = { isDarkTheme = it }
            )
        }
    }
}