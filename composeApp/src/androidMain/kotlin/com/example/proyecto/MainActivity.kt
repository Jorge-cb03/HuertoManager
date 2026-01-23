package com.example.proyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyecto.di.AppModule
import com.example.proyecto.data.local.DatabaseFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // 1. Instanciamos la fábrica
        val factory = DatabaseFactory(applicationContext)

        // 2. Creamos y Construimos la base de datos (.create() devuelve el Builder, .build() la crea)
        val database = factory.create().build()

        // 3. Ahora sí, inicializamos el AppModule con la base de datos real
        AppModule.initialize(database)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}