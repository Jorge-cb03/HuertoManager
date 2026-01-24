package com.example.proyecto.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// Subimos versión y añadimos BancalEntity
@Database(
    entities = [JardineraEntity::class, DiarioEntity::class, ProductoEntity::class, BancalEntity::class],
    version = 2 // IMPORTANTE: Al cambiar esquema, subimos versión.
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun huertaDao(): HuertaDao
}