package com.example.proyecto.data.database.dao

import androidx.room.*
import com.example.proyecto.data.database.entity.BancalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BancalDao {
    @Query("SELECT * FROM bancales WHERE jardineraId = :jardineraId ORDER BY fila, columna")
    fun getBancalesByJardinera(jardineraId: Long): Flow<List<BancalEntity>>

    @Query("SELECT * FROM bancales WHERE id = :bancalId")
    suspend fun getBancalById(bancalId: Long): BancalEntity?

    // Cambiamos @Update por @Insert con Replace
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBancal(bancal: BancalEntity)

    @Query("UPDATE bancales SET cultivoSlug = NULL, nombreCultivo = NULL, imagenUrl = NULL, fechaSiembra = NULL, diasParaCosecha = NULL WHERE id = :bancalId")
    suspend fun clearBancal(bancalId: Long)
}