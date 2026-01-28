package com.example.proyecto.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.proyecto.data.database.dao.BancalDao
import com.example.proyecto.data.database.dao.EntradaDiarioDao
import com.example.proyecto.data.database.dao.JardineraDao
import com.example.proyecto.data.database.dao.ProductoDao
import com.example.proyecto.data.database.entity.BancalEntity
import com.example.proyecto.data.database.entity.EntradaDiarioEntity
import com.example.proyecto.data.database.entity.JardineraEntity
import com.example.proyecto.data.database.entity.ProductoEntity

@Database(
    entities = [
        JardineraEntity::class,
        BancalEntity::class,
        EntradaDiarioEntity::class,
        ProductoEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jardineraDao(): JardineraDao
    abstract fun bancalDao(): BancalDao
    abstract fun entradaDiarioDao(): EntradaDiarioDao
    abstract fun productoDao(): ProductoDao
}