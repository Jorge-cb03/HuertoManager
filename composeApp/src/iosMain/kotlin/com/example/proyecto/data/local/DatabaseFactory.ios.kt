package com.example.proyecto.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory

actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<AppDatabase> {
        val dbFilePath = NSHomeDirectory() + "/Documents/huerta.db"
        return Room.databaseBuilder<AppDatabase>(
            name = dbFilePath,
            factory = { AppDatabase::class.instantiateImpl() } // Truco necesario en iOS para Room
        )
    }
}