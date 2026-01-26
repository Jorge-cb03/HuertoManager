package com.example.proyecto.ui.garden

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.repository.HuertaRepository
import com.example.proyecto.domain.model.Bancal
import com.example.proyecto.domain.model.EntradaDiario
import com.example.proyecto.ui.theme.GreenPrimary
import com.example.proyecto.ui.theme.RedDanger
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.*

class BancalDetailViewModel(
    private val repository: HuertaRepository,
    private val bancalId: String
) : ViewModel() {

    // Cargamos SOLO este bancal
    val bancal: StateFlow<Bancal?> = repository.getBancalById(bancalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Cargamos SOLO el historial de este bancal
    val historial: StateFlow<List<EntradaDiario>> = repository.getDiarioPorBancal(bancalId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // NUEVO: Acción de regar desde el detalle
    fun regar() {
        val b = bancal.value ?: return
        val nombre = b.planta?.nombre ?: "Planta"
        viewModelScope.launch {
            repository.regarBancal(bancalId, nombre)
        }
    }

    // NUEVO: Lógica de Próximo Riego
    fun getInfoRiego(bancal: Bancal): Pair<String, Color> {
        val ultimoRiego = bancal.fechaUltimoRiego ?: return Pair("Sin datos", Color.Gray)

        // Frecuencia simulada (en V2 esto vendría de una BD de plantas)
        val frecuenciaDias = when {
            bancal.planta?.nombre?.contains("Tomate", true) == true -> 2
            bancal.planta?.nombre?.contains("Lechuga", true) == true -> 1 // Requiere mucha agua
            bancal.planta?.nombre?.contains("Cactus", true) == true -> 15
            else -> 3 // Estándar
        }

        val instantUltimo = Instant.fromEpochMilliseconds(ultimoRiego)
        // Calculamos días pasados
        val diasDesdeRiego = instantUltimo.daysUntil(Clock.System.now(), TimeZone.currentSystemDefault())
        val diasRestantes = frecuenciaDias - diasDesdeRiego

        return when {
            diasRestantes < 0 -> Pair("¡Riego atrasado ${diasRestantes * -1} días!", RedDanger)
            diasRestantes == 0 -> Pair("Regar HOY", RedDanger)
            else -> Pair("Riego en $diasRestantes días", GreenPrimary)
        }
    }

    // Recomendaciones simuladas
    fun getRecomendaciones(nombrePlanta: String?): String {
        return when {
            nombrePlanta == null -> "Sin planta."
            nombrePlanta.contains("Tomate", true) -> "• Riego frecuente pero sin mojar hojas.\n• Entutorar cuando crezca.\n• Podar chupones."
            nombrePlanta.contains("Lechuga", true) -> "• Riego ligero y frecuente.\n• Proteger del sol fuerte directo.\n• Cosechar antes de que espigue."
            nombrePlanta.contains("Zanahoria", true) -> "• Mantener tierra húmeda para germinar.\n• Aclarar brotes si están muy juntos.\n• Suelo suelto y sin piedras."
            else -> "• Mantener humedad constante.\n• Revisar plagas semanalmente.\n• Abonar cada 15 días en crecimiento."
        }
    }
}