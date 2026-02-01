package com.example.proyecto.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val dateTimeEpochMillis: Long,
    val isUrgent: Boolean
)