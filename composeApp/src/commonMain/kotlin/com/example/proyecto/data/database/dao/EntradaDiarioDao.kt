package com.example.proyecto.data.database.dao

import androidx.room.*
import com.example.proyecto.data.database.entity.EntradaDiarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EntradaDiarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntrada(entrada: EntradaDiarioEntity)

    // Para el detalle del bancal (ya lo tienes)
    @Query("SELECT * FROM entradas_diario WHERE bancalId = :bancalId ORDER BY fecha DESC")
    fun getDiarioByBancal(bancalId: Long): Flow<List<EntradaDiarioEntity>>

    // NUEVO: Para el Diario General
    @Query("SELECT * FROM entradas_diario ORDER BY fecha DESC")
    fun getAllEntradas(): Flow<List<EntradaDiarioEntity>>

    @Query("SELECT * FROM entradas_diario WHERE id = :id")
    suspend fun getEntradaById(id: Long): EntradaDiarioEntity?
    @Query("DELETE FROM entradas_diario WHERE id = :id")
    suspend fun deleteById(id: Long)
}