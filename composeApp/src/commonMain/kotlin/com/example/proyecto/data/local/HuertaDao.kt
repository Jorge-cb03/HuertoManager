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

    @Query("SELECT * FROM diario ORDER BY fecha DESC")
    fun getDiarioGlobal(): Flow<List<DiarioEntity>>

    // --- PRODUCTOS ---
    @Query("SELECT * FROM productos")
    fun getProductos(): Flow<List<ProductoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducto(producto: ProductoEntity)

    @Query("DELETE FROM productos WHERE id = :id")
    suspend fun deleteProducto(id: String)
}