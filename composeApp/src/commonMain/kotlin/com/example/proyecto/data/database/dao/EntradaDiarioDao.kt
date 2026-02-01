package com.example.proyecto.data.database.dao

import androidx.room.*
import com.example.proyecto.data.database.entity.EntradaDiarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EntradaDiarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntrada(entrada: EntradaDiarioEntity)

    @Query("SELECT * FROM entradas_diario WHERE bancalId = :bancalId ORDER BY fecha DESC")
    fun getDiarioByBancal(bancalId: Long): Flow<List<EntradaDiarioEntity>>

    @Query("SELECT * FROM entradas_diario ORDER BY fecha DESC")
    fun getAllEntradas(): Flow<List<EntradaDiarioEntity>>

    @Query("SELECT * FROM entradas_diario WHERE descripcion LIKE '%' || :query || '%' OR tipoAccion = :tipo ORDER BY fecha DESC")
    fun buscarEntradas(query: String, tipo: String): Flow<List<EntradaDiarioEntity>>
    @Query("SELECT * FROM entradas_diario WHERE id = :id")
    suspend fun getEntradaById(id: Long): EntradaDiarioEntity?

    @Query("DELETE FROM entradas_diario WHERE id = :id")
    suspend fun deleteById(id: Long)
}