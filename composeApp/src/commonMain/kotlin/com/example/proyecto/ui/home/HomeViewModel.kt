package com.example.proyecto.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.repository.HuertaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.random.Random

data class HomeStats(
    val saludHuerto: Int,
    val plantasActivas: Int,
    val totalHuecos: Int,
    val alertasFuturas: List<EntradaDiario>, // CAMBIO DE NOMBRE PARA QUE QUEDE CLARO
    val temperatura: String,
    val humedad: String,
    val uv: String
)

class HomeViewModel(
    private val repository: HuertaRepository
) : ViewModel() {

    // Combinamos datos del huerto con ALERTAS (GetAlertas trae solo eventos > Ahora)
    val stats: StateFlow<HomeStats> = combine(
        repository.bancalesGlobales,
        repository.getAlertas()
    ) { bancales, alertas ->

        val total = bancales.size
        // Datos simulados de sensores
        val temp = "${Random.nextInt(18, 30)}°C"
        val hum = "${Random.nextInt(40, 80)}%"
        val uvIndex = listOf("Bajo", "Medio", "Alto").random()

        if (total == 0) {
            HomeStats(0, 0, 0, alertas, temp, hum, uvIndex)
        } else {
            val ocupados = bancales.count { it.estado.name == "OCUPADO" }
            val saludCalculada = if (ocupados > 0) 90 else 0

            HomeStats(
                saludHuerto = saludCalculada,
                plantasActivas = ocupados,
                totalHuecos = total,
                alertasFuturas = alertas,
                temperatura = temp,
                humedad = hum,
                uv = uvIndex
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeStats(0, 0, 0, emptyList(), "--", "--", "--"))
}