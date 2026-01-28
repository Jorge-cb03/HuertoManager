package com.example.proyecto.data.api

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class OpenFarmService {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // Ignora campos extras del backend
                isLenient = true
            })
        }
        // Evita crashes por respuestas de error del servidor
        install(HttpTimeout) { requestTimeoutMillis = 15000 }
        defaultRequest {
            header("User-Agent", "HuertoManagerApp")
        }
    }

    // El controlador pide un filtro de más de 2 caracteres
    suspend fun buscarCultivo(query: String) =
        client.get("https://openfarm.cc/api/v1/crops/") {
            url { parameters.append("filter", query) }
        }
}