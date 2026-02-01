package com.example.proyecto.data.database.dao

import androidx.room.*
import com.example.proyecto.data.database.entity.JardineraEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JardineraDao {
    @Query("SELECT * FROM jardineras WHERE estaArchivada = 0")
    fun getJardinerasActivas(): Flow<List<JardineraEntity>>

    // Nuevo: Consultar jardineras archivadas
    @Query("SELECT * FROM jardineras WHERE estaArchivada = 1")
    fun getJardinerasArchivadas(): Flow<List<JardineraEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJardinera(jardinera: JardineraEntity): Long

    @Update
    suspend fun updateJardinera(jardinera: JardineraEntity)

    @Delete
    suspend fun deleteJardinera(jardinera: JardineraEntity)
}