package com.example.proyecto.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HuertaDao {
    // --- JARDINERAS ---

    // Flow permite que si la base de datos cambia, la UI se actualice sola "en vivo"
    @Query("SELECT * FROM jardineras")
    fun getJardineras(): Flow<List<JardineraEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJardinera(jardinera: JardineraEntity)

    @Query("DELETE FROM jardineras WHERE id = :id")
    suspend fun deleteJardinera(id: String)

    // --- DIARIO ---

    @Query("SELECT * FROM diario WHERE jardineraId = :jardineraId ORDER BY fecha DESC")
    fun getDiarioPorJardinera(jardineraId: String): Flow<List<DiarioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntrada(entrada: DiarioEntity)

    @Query("SELECT * FROM jardineras WHERE id = :id")
    suspend fun getJardineraById(id: String): JardineraEntity?
}