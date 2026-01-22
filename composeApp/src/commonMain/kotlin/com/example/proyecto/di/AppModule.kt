package com.example.proyecto.di

import com.example.proyecto.data.local.AppDatabase
import com.example.proyecto.data.local.DatabaseFactory
import com.example.proyecto.data.repository.HuertaRepository
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

object AppModule {
    lateinit var database: AppDatabase

    // Lazy: El repositorio solo se crea cuando alguien lo pide por primera vez
    val huertaRepository: HuertaRepository by lazy {
        HuertaRepository(database.huertaDao())
    }

    // Esta función la llamaremos al arrancar la App (en Android y en iOS)
    fun initialize(factory: DatabaseFactory) {
        database = factory.create()
            .setDriver(BundledSQLiteDriver()) // Driver SQLite multiplataforma
            .build()
    }
}