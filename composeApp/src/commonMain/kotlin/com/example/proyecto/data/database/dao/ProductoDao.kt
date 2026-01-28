package com.example.proyecto.data.database.dao

import androidx.room.*
import com.example.proyecto.data.database.entity.ProductoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Query("SELECT * FROM productos")
    fun getAllProductos(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos WHERE openFarmSlug = :slug LIMIT 1")
    suspend fun getProductoBySlug(slug: String): ProductoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducto(producto: ProductoEntity)

    @Query("UPDATE productos SET stock = stock - 1 WHERE openFarmSlug = :slug AND stock > 0")
    suspend fun decreaseStock(slug: String)
}