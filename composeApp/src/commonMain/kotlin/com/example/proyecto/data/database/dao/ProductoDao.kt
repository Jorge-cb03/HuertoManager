package com.example.proyecto.data.database.dao

import androidx.room.*
import com.example.proyecto.data.database.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducto(producto: ProductoEntity)

    @Update
    suspend fun updateProducto(producto: ProductoEntity)

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun getAllProductos(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getProductoById(id: Long): ProductoEntity?

    @Query("DELETE FROM productos WHERE id = :id")
    suspend fun deleteProductoById(id: Long)

    // CAMBIO: Buscar por Slug (String)
    @Query("SELECT * FROM productos WHERE perenualId = :id LIMIT 1")
    suspend fun getProductoByPerenualId(id: Int): ProductoEntity?
}