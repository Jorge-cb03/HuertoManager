package com.example.proyecto.data.database.dao

import androidx.room.*
import com.example.proyecto.data.database.entity.EntradaDiarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EntradaDiarioDao {
    @Query("SELECT * FROM diarios WHERE bancalId = :bancalId ORDER BY fecha DESC")
    fun getDiarioByBancal(bancalId: Long): Flow<List<EntradaDiarioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntrada(entrada: EntradaDiarioEntity)
}