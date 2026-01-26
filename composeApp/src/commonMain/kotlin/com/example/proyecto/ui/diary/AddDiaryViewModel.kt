package com.example.proyecto.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.data.repository.HuertaRepository
import com.example.proyecto.domain.model.EntradaDiario
import com.example.proyecto.domain.model.TipoEvento
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class AddDiaryViewModel(
    private val repository: HuertaRepository
) : ViewModel() {

    fun saveEntry(title: String, description: String, type: TipoEvento, dateTime: LocalDateTime) {
        if (title.isBlank()) return

        viewModelScope.launch {
            try {
                // Obtenemos milisegundos de la fecha seleccionada
                val millis = dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                val id = repository.generateId()

                // Creamos la alerta (se distingue porque tiene fecha futura y jardinera "manual")
                val entrada = EntradaDiario(
                    id = id,
                    jardineraId = "manual",
                    bancalId = null,
                    fecha = millis,
                    tipo = type,
                    titulo = title,
                    descripcion = description,
                    fotoUrl = null
                )

                repository.registrarEvento(entrada)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}