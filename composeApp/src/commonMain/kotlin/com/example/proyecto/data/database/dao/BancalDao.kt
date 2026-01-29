package com.example.proyecto.data.database.dao

import androidx.room.*
import com.example.proyecto.data.database.entity.BancalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BancalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBancal(bancal: BancalEntity): Long // Cambiado Unit por Long

    @Query("SELECT * FROM bancales WHERE jardineraId = :jardineraId")
    fun getBancalesByJardinera(jardineraId: Long): Flow<List<BancalEntity>>

    @Query("SELECT * FROM bancales WHERE id = :id")
    suspend fun getBancalById(id: Long): BancalEntity?
}