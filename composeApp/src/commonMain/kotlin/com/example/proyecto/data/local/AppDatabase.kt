package com.example.proyecto.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// IMPORTANTE:
// 1. Añadimos ProductoEntity::class a la lista
// 2. Subimos la versión a 2 (porque hemos cambiado la estructura)
@Database(
    entities = [
        JardineraEntity::class,
        DiarioEntity::class,
        ProductoEntity::class // <--- AÑADIDO
    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    // Esto nos da acceso a las funciones
    abstract fun huertaDao(): HuertaDao
}