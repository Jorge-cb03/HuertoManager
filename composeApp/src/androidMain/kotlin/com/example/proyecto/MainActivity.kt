package com.example.proyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
// Importación directa de la variable
import com.example.proyecto.data.database.appContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Asignamos el contexto antes de cargar la App para que Room no falle
        appContext = applicationContext

        setContent { App() }
    }
}