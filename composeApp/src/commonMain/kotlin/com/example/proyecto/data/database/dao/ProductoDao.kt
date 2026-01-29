package com.example.proyecto.data.database.dao

import androidx.room.*
import com.example.proyecto.data.database.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Query("SELECT * FROM productos")
    fun getAllProductos(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getProductoById(id: Long): ProductoEntity?

    @Query("SELECT * FROM productos WHERE openFarmSlug = :slug LIMIT 1")
    suspend fun getProductoBySlug(slug: String): ProductoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducto(producto: ProductoEntity)

    @Update
    suspend fun updateProducto(producto: ProductoEntity)
}