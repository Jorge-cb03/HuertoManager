package com.example.proyecto.data.repository

import com.example.proyecto.data.local.DiarioEntity
import com.example.proyecto.data.local.HuertaDao
import com.example.proyecto.data.local.JardineraEntity
import com.example.proyecto.domain.model.EntradaDiario
import com.example.proyecto.domain.model.Jardinera
import com.example.proyecto.domain.model.EstadoJardinera
import com.example.proyecto.domain.model.TipoEntrada
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant            // <--- NUEVO IMPORT
import kotlinx.datetime.TimeZone           // <--- NUEVO IMPORT
import kotlinx.datetime.toLocalDateTime    // <--- NUEVO IMPORT
import kotlin.random.Random

class HuertaRepository(private val dao: HuertaDao) {

    // TRANSFORMACIÓN: Convertimos Entity (BD) -> Domain (UI)
    val jardineras = dao.getJardineras().map { entities ->
        entities.map { entity ->
            Jardinera(
                id = entity.id,
                nombre = entity.nombre,
                cultivo = entity.cultivo,
                estado = EstadoJardinera.valueOf(entity.estado), // Convertimos String a Enum
                icon = entity.icon
            )
        }
    }

    suspend fun crearJardinera(nombre: String) {
        val nueva = JardineraEntity(
            id = generateUniqueId(),
            nombre = nombre,
            cultivo = null,
            estado = "VACIO",
            icon = "🌱"
        )
        dao.insertJardinera(nueva)
    }

    private fun generateUniqueId(): String {
        val charPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..16)
            .map { charPool[Random.nextInt(0, charPool.length)] }
            .joinToString("")
    }

    // 1. Obtener una jardinera específica
    suspend fun getJardinera(id: String): Jardinera? {
        val entity = dao.getJardineraById(id) ?: return null
        return Jardinera(
            id = entity.id,
            nombre = entity.nombre,
            cultivo = entity.cultivo,
            estado = EstadoJardinera.valueOf(entity.estado),
            icon = entity.icon
        )
    }

    // 2. Escuchar el diario de esa jardinera
    fun getDiarioFlow(jardineraId: String): Flow<List<EntradaDiario>> {
        return dao.getDiarioPorJardinera(jardineraId).map { entities ->
            entities.map {
                EntradaDiario(
                    id = it.id,
                    // CORRECCIÓN AQUÍ: Convertimos Long (Milisegundos) -> LocalDate
                    fecha = Instant.fromEpochMilliseconds(it.fecha)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .date,
                    titulo = it.titulo,
                    descripcion = it.descripcion,
                    tipo = TipoEntrada.valueOf(it.tipo),
                    fotoUrl = it.fotoUrl
                )
            }
        }
    }

    // 3. Crear una entrada de diario (para probar)
    suspend fun addEntradaDemo(jardineraId: String) {
        dao.insertEntrada(
            DiarioEntity(
                id = generateUniqueId(),
                jardineraId = jardineraId,
                fecha = System.currentTimeMillis(), // Aquí guardamos Long, eso está bien
                titulo = "Riego Automático",
                descripcion = "Se ha regado durante 15 min.",
                tipo = "RIEGO"
            )
        )
    }
}