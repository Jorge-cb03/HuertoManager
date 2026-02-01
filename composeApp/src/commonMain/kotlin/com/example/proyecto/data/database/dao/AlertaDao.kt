package com.example.proyecto.data.database.dao

import androidx.room.*
import com.example.proyecto.data.database.entity.AlertaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {
    @Query("SELECT * FROM alertas ORDER BY dateTimeEpochMillis ASC")
    fun getAllAlerts(): Flow<List<AlertaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertaEntity)

    @Update
    suspend fun updateAlert(alert: AlertaEntity)

    @Delete
    suspend fun deleteAlert(alert: AlertaEntity)
}