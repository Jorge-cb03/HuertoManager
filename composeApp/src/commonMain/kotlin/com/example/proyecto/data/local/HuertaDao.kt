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

    @Query("DELETE FROM jardineras WHERE id = :id")
    suspend fun deleteJardinera(id: String)

    @Query("UPDATE jardineras SET nombre = :nombre WHERE id = :id")
    suspend fun updateNombreJardinera(id: String, nombre: String)

    // --- BANCALES ---
    @Query("SELECT * FROM bancales WHERE jardineraId = :jardineraId ORDER BY indice ASC")
    fun getBancales(jardineraId: String): Flow<List<BancalEntity>>

    @Query("SELECT * FROM bancales")
    fun getAllBancales(): Flow<List<BancalEntity>>

    // NUEVO: Obtener un solo bancal
    @Query("SELECT * FROM bancales WHERE id = :id")
    fun getBancalById(id: String): Flow<BancalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBancal(bancal: BancalEntity)

    // NUEVO: Borrar un bancal individual (Ocultar hueco)
    @Query("DELETE FROM bancales WHERE id = :id")
    suspend fun deleteBancal(id: String)

    @Query("UPDATE bancales SET estado = :estado, plantaNombre = :nombre, plantaVariedad = :variedad, plantaTipo = :tipo, fechaSiembra = :fecha WHERE id = :id")
    suspend fun updateBancalSiembra(id: String, estado: String, nombre: String, variedad: String, tipo: String, fecha: Long)

    @Query("UPDATE bancales SET estado = 'VACIO', plantaNombre = NULL, plantaVariedad = NULL, fechaSiembra = NULL WHERE id = :id")
    suspend fun updateBancalLimpieza(id: String)

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

    // --- PRODUCTOS ---
    @Query("SELECT * FROM productos")
    fun getProductos(): Flow<List<ProductoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducto(producto: ProductoEntity)

    @Query("DELETE FROM productos WHERE id = :id")
    suspend fun deleteProducto(id: String)

    @Query("UPDATE productos SET cantidad = :nuevaCantidad WHERE id = :id")
    suspend fun updateProductoStock(id: String, nuevaCantidad: String)

    // --- DIARIO HISTÓRICO (PASADO) ---
    @Query("SELECT * FROM diario WHERE fecha <= :now ORDER BY fecha DESC")
    fun getHistorial(now: Long): Flow<List<DiarioEntity>>

    // --- ALERTAS (FUTURO) ---
    @Query("SELECT * FROM diario WHERE fecha > :now ORDER BY fecha ASC")
    fun getAlertas(now: Long): Flow<List<DiarioEntity>>

    @Query("UPDATE jardineras SET filas = :nuevasFilas WHERE id = :id")
    suspend fun updateJardineraFilas(id: String, nuevasFilas: Int)
}