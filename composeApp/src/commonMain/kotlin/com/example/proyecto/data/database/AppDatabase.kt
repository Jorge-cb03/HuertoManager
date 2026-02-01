package com.example.proyecto.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.proyecto.data.database.dao.JardineraDao
import com.example.proyecto.data.database.dao.BancalDao
import com.example.proyecto.data.database.dao.ProductoDao
import com.example.proyecto.data.database.dao.EntradaDiarioDao
import com.example.proyecto.data.database.dao.AlertDao // <--- IMPORT CRÍTICO
import com.example.proyecto.data.database.dao.UsuarioDao
import com.example.proyecto.data.database.entity.JardineraEntity
import com.example.proyecto.data.database.entity.BancalEntity
import com.example.proyecto.data.database.entity.ProductoEntity
import com.example.proyecto.data.database.entity.EntradaDiarioEntity
import com.example.proyecto.data.database.entity.AlertaEntity
import com.example.proyecto.data.database.entity.UsuarioEntity

@Database(
    entities = [
        JardineraEntity::class,
        BancalEntity::class,
        ProductoEntity::class,
        EntradaDiarioEntity::class,
        AlertaEntity::class,
        UsuarioEntity::class
    ],
    version = 9
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun jardineraDao(): JardineraDao
    abstract fun bancalDao(): BancalDao
    abstract fun productoDao(): ProductoDao
    abstract fun entradaDiarioDao(): EntradaDiarioDao
    abstract fun alertDao(): AlertDao
    abstract fun usuarioDao(): UsuarioDao
}