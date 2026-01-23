package com.example.proyecto.di

import com.example.proyecto.data.local.AppDatabase
import com.example.proyecto.data.repository.AuthRepository // IMPORTAR
import com.example.proyecto.data.repository.HuertaRepository

object AppModule {
    private lateinit var database: AppDatabase

    fun initialize(database: AppDatabase) {
        this.database = database
    }

    // Repositorio de Datos
    val huertaRepository: HuertaRepository by lazy {
        HuertaRepository(database.huertaDao())
    }

    // Repositorio de Auth (NUEVO)
    val authRepository: AuthRepository by lazy {
        AuthRepository()
    }
}