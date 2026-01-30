package com.example.proyecto.di

import com.example.proyecto.data.database.AppDatabase
import com.example.proyecto.data.database.getDatabaseBuilder
import com.example.proyecto.data.repository.AuthRepository
import com.example.proyecto.data.repository.JardineraRepository
import com.example.proyecto.ui.garden.GardenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<AppDatabase> { getDatabaseBuilder().build() }

    single { get<AppDatabase>().jardineraDao() }
    single { get<AppDatabase>().bancalDao() }
    single { get<AppDatabase>().productoDao() }
    single { get<AppDatabase>().entradaDiarioDao() }
    // AlertDao es accesible a través de AppDatabase internamente, no hace falta exponerlo si no quieres,
    // pero el Repo ya lo usa a través de 'db'.

    // Repositorio autónomo (sin API)
    single { JardineraRepository(db = get<AppDatabase>()) }

    single { AuthRepository() } // Añadimos el repo de Firebase

    viewModel { GardenViewModel(repository = get<JardineraRepository>()) }
}