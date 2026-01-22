package com.example.proyecto

import androidx.compose.ui.window.ComposeUIViewController
import com.example.proyecto.di.AppModule
import com.example.proyecto.data.local.DatabaseFactory

fun MainViewController() = ComposeUIViewController(
    configure = {
        AppModule.initialize(DatabaseFactory())
    }
) { App() }