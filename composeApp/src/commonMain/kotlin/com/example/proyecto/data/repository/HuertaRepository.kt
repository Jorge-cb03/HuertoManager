package com.example.proyecto.data.repository

import com.example.proyecto.data.local.HuertaDao
import com.example.proyecto.data.local.JardineraEntity
import com.example.proyecto.domain.model.Jardinera
import com.example.proyecto.domain.model.EstadoJardinera
import kotlinx.coroutines.flow.map
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
        // Aquí generamos el ID y guardamos
        val nueva = JardineraEntity(
            id = generateUniqueId(), // Implementa una func simple que devuelva System.currentTimeMillis().toString()
            nombre = nombre,
            cultivo = null,
            estado = "VACIO",
            icon = "🌱"
        )
        dao.insertJardinera(nueva)
    }

    // Genera un ID aleatorio usando solo Kotlin estándar
    private fun generateUniqueId(): String {
        val charPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..16)
            .map { charPool[Random.nextInt(0, charPool.length)] }
            .joinToString("")
    }}