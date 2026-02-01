package com.example.proyecto.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entradas_diario")
data class EntradaDiarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bancalId: Long,
    val tipoAccion: String,
    val descripcion: String,
    val fecha: Long,
    val foto: ByteArray? = null // NUEVO CAMPO
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EntradaDiarioEntity

        if (id != other.id) return false
        if (bancalId != other.bancalId) return false
        if (tipoAccion != other.tipoAccion) return false
        if (descripcion != other.descripcion) return false
        if (fecha != other.fecha) return false
        if (foto != null) {
            if (other.foto == null) return false
            if (!foto.contentEquals(other.foto)) return false
        } else if (other.foto != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + bancalId.hashCode()
        result = 31 * result + tipoAccion.hashCode()
        result = 31 * result + descripcion.hashCode()
        result = 31 * result + fecha.hashCode()
        result = 31 * result + (foto?.contentHashCode() ?: 0)
        return result
    }
}