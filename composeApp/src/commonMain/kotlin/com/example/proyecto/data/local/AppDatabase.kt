package com.example.proyecto.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// Listamos todas las entidades y subimos la versión si cambiamos algo
@Database(entities = [JardineraEntity::class, DiarioEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    // Esto nos da acceso a las funciones
    abstract fun huertaDao(): HuertaDao
}