package com.example.proyecto.di

import com.example.proyecto.data.api.OpenFarmService
import com.example.proyecto.data.database.DatabaseProvider
import com.example.proyecto.data.repository.JardineraRepository
import com.example.proyecto.ui.garden.GardenViewModel
import com.example.proyecto.data.database.AppDatabase
import com.example.proyecto.data.database.getDatabaseBuilder
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

val appModule = module {
    single<AppDatabase> {
        getDatabaseBuilder().build()
    }

    // 1. Instancia única de la base de datos
    single { DatabaseProvider.getDatabase() }

    // 2. DAOs
    single { get<AppDatabase>().jardineraDao() }
    single { get<AppDatabase>().bancalDao() }
    single { get<AppDatabase>().productoDao() }
    single { get<AppDatabase>().entradaDiarioDao() }

    // 3. API Service
    single { OpenFarmService() }

    // 4. Repositorio
    single { JardineraRepository(get(), get()) }

    // 5. ViewModel (Cambiado para máxima compatibilidad)
    viewModel { GardenViewModel(get()) }
}