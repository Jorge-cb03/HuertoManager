package com.example.proyecto.data.database.dao

import androidx.room.*
import com.example.proyecto.data.database.entity.JardineraEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JardineraDao {
    @Query("SELECT * FROM jardineras")
    fun getAllJardineras(): Flow<List<JardineraEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJardinera(jardinera: JardineraEntity): Long

    @Delete
    suspend fun deleteJardinera(jardinera: JardineraEntity)
}