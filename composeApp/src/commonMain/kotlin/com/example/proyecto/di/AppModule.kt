package com.example.proyecto.di

import com.example.proyecto.data.local.AppDatabase
import com.example.proyecto.data.repository.HuertaRepository

// Tu contenedor de dependencias manual (Simple y efectivo para KMP)
object AppModule {
    private lateinit var database: AppDatabase

    fun initialize(database: AppDatabase) {
        this.database = database
    }

    // Instancia única del Repositorio
    val huertaRepository: HuertaRepository by lazy {
        HuertaRepository(database.huertaDao())
    }
}