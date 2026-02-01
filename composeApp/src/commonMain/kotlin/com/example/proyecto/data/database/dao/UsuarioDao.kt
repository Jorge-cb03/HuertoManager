package com.example.proyecto.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyecto.data.database.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    // FIX: Room devuelve una lista o un objeto nulo, no un Flow directo si puede ser nulo en la primera emisión sin datos.
    // Lo más seguro es devolver Flow<UsuarioEntity?> y manejar el nulo en el repositorio.
    @Query("SELECT * FROM usuario WHERE id = 1 LIMIT 1")
    fun getUsuario(): Flow<UsuarioEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUsuario(usuario: UsuarioEntity)
}