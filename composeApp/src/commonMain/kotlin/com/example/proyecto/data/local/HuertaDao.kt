package com.example.proyecto.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HuertaDao {
    // --- JARDINERAS ---
    @Query("SELECT * FROM jardineras")
    fun getJardineras(): Flow<List<JardineraEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJardinera(jardinera: JardineraEntity)

    // --- BANCALES (GRID) ---
    @Query("SELECT * FROM bancales WHERE jardineraId = :jardineraId ORDER BY indice ASC")
    fun getBancales(jardineraId: String): Flow<List<BancalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBancal(bancal: BancalEntity)

    // --- DIARIO ---
    @Query("SELECT * FROM diario ORDER BY fecha DESC")
    fun getDiarioGlobal(): Flow<List<DiarioEntity>>

    @Query("SELECT * FROM diario WHERE jardineraId = :jardineraId ORDER BY fecha DESC")
    fun getDiarioPorJardinera(jardineraId: String): Flow<List<DiarioEntity>>

    @Query("SELECT * FROM diario WHERE bancalId = :bancalId ORDER BY fecha DESC")
    fun getDiarioPorBancal(bancalId: String): Flow<List<DiarioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntrada(entrada: DiarioEntity)

    @Query("DELETE FROM diario WHERE id = :id")
    suspend fun deleteEntrada(id: String)

    // --- PRODUCTOS (CATÁLOGO) ---
    @Query("SELECT * FROM productos")
    fun getProductos(): Flow<List<ProductoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducto(producto: ProductoEntity)

    @Query("DELETE FROM productos WHERE id = :id")
    suspend fun deleteProducto(id: String)
}