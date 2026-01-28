package com.example.proyecto.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver

// 1. DECLARAMOS LA VARIABLE AQUÍ (Esto quita el error de MainActivity)
lateinit var appContext: Context

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    // Usamos el contexto que MainActivity nos dará al arrancar
    val dbFile = appContext.getDatabasePath("huerto.db")

    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    ).setDriver(AndroidSQLiteDriver()) // Driver nativo para que funcione el Inspector
}