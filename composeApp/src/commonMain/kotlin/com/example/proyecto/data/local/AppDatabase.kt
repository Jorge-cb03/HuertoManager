package com.example.proyecto.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [JardineraEntity::class, DiarioEntity::class, ProductoEntity::class, BancalEntity::class],
    version = 3 // <--- SUBIR A 3 (Y recuerda desinstalar la app del emulador)
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun huertaDao(): HuertaDao
}