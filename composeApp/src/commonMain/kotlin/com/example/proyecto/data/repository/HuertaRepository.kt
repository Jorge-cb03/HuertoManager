package com.example.proyecto.data.repository

import com.example.proyecto.data.local.*
import com.example.proyecto.data.remote.FirebaseClient
import com.example.proyecto.data.remote.model.*
import com.example.proyecto.domain.model.*
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HuertaRepository(
    private val dao: HuertaDao
) {
    // ----------------------------------------------------------------
    // 1. LECTURA (Dominio)
    // ----------------------------------------------------------------

    // JARDINERAS (Solo contenedores)
    val jardineras = dao.getJardineras().map { entities ->
        entities.map { entity ->
            Jardinera(
                id = entity.id,
                nombre = entity.nombre,
                filas = entity.filas,
                columnas = entity.columnas,
                bancales = emptyList() // Se cargarán en detalle
            )
        }
    }

    val diarioGlobal = dao.getDiarioGlobal().map { mapDiario(it) }

    // Obtener una Jardinera COMPLETA con sus Bancales (Para la pantalla GardenScreen)
    fun getJardineraConBancales(jardineraId: String) = dao.getBancales(jardineraId).map { bancalEntities ->
        bancalEntities.map { entity ->
            Bancal(
                id = entity.id,
                jardineraId = entity.jardineraId,
                indice = entity.indice,
                estado = try { EstadoBancal.valueOf(entity.estado) } catch (e: Exception) { EstadoBancal.VACIO },
                fechaSiembra = entity.fechaSiembra,
                fechaUltimoRiego = entity.fechaUltimoRiego,
                fechaUltimoAbono = entity.fechaUltimoAbono,
                planta = if (entity.plantaNombre != null) {
                    Planta(
                        nombre = entity.plantaNombre,
                        variedad = entity.plantaVariedad ?: "",
                        tipo = try { TipoCultivo.valueOf(entity.plantaTipo ?: "OTRO") } catch (e: Exception) { TipoCultivo.OTRO },
                        imagenRes = entity.plantaIcono ?: "plant_default"
                    )
                } else null
            )
        }
    }

    // DIARIO (Filtrado inteligente)
    fun getDiarioPorJardinera(jardineraId: String) = dao.getDiarioPorJardinera(jardineraId).map { mapDiario(it) }
    fun getDiarioPorBancal(bancalId: String) = dao.getDiarioPorBancal(bancalId).map { mapDiario(it) }

    private fun mapDiario(entities: List<DiarioEntity>): List<EntradaDiario> {
        return entities.map { entity ->
            EntradaDiario(
                id = entity.id,
                jardineraId = entity.jardineraId,
                bancalId = entity.bancalId,
                fecha = entity.fecha,
                tipo = try { TipoEvento.valueOf(entity.tipo) } catch (e: Exception) { TipoEvento.NOTA },
                titulo = entity.titulo,
                descripcion = entity.descripcion,
                fotoUrl = entity.fotoUrl
            )
        }
    }

    // ----------------------------------------------------------------
    // 2. SINCRONIZACIÓN (Motor)
    // ----------------------------------------------------------------
    fun startSync() {
        CoroutineScope(Dispatchers.IO).launch {
            // A. Jardineras
            launch {
                try {
                    FirebaseClient.firestore.collection("jardineras").snapshots.collect { qs ->
                        qs.documents.map { it.data<JardineraRemote>() }.forEach { remote ->
                            dao.insertJardinera(JardineraEntity(remote.id, remote.nombre, remote.filas, remote.columnas, remote.icon))
                        }
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }

            // B. Bancales (¡CRÍTICO!)
            launch {
                try {
                    FirebaseClient.firestore.collection("bancales").snapshots.collect { qs ->
                        qs.documents.map { it.data<BancalRemote>() }.forEach { remote ->
                            dao.insertBancal(BancalEntity(
                                id = remote.id,
                                jardineraId = remote.jardineraId,
                                indice = remote.indice,
                                estado = remote.estado,
                                fechaSiembra = remote.fechaSiembra,
                                fechaUltimoRiego = remote.fechaUltimoRiego,
                                fechaUltimoAbono = remote.fechaUltimoAbono,
                                plantaNombre = remote.plantaNombre,
                                plantaVariedad = remote.plantaVariedad,
                                plantaTipo = remote.plantaTipo,
                                plantaIcono = remote.plantaIcono
                            ))
                        }
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }

            // C. Diario
            launch {
                try {
                    FirebaseClient.firestore.collection("diario").snapshots.collect { qs ->
                        qs.documents.map { it.data<DiarioRemote>() }.forEach { remote ->
                            dao.insertEntrada(DiarioEntity(remote.id, remote.jardineraId, remote.bancalId, remote.fecha, remote.tipo, remote.titulo, remote.descripcion, remote.fotoUrl))
                        }
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
    }

    // ----------------------------------------------------------------
    // 3. ESCRITURA (Acciones)
    // ----------------------------------------------------------------

    // Crear Jardinera (Inicializa los bancales vacíos en la nube)
    suspend fun crearJardinera(nombre: String, filas: Int = 2, columnas: Int = 4) {
        val jardineraId = generateId()

        // 1. Guardar Jardinera
        val remoteJardinera = JardineraRemote(id = jardineraId, nombre = nombre, filas = filas, columnas = columnas)
        FirebaseClient.firestore.collection("jardineras").document(jardineraId).set(remoteJardinera)

        // 2. Inicializar Bancales Vacíos
        val totalHuecos = filas * columnas
        for (i in 0 until totalHuecos) {
            val bancalId = generateId()
            val remoteBancal = BancalRemote(
                id = bancalId,
                jardineraId = jardineraId,
                indice = i,
                estado = "VACIO"
            )
            FirebaseClient.firestore.collection("bancales").document(bancalId).set(remoteBancal)
        }
    }

    // Registrar Evento en Diario (Y actualizar estado del bancal si procede)
    suspend fun registrarEvento(evento: EntradaDiario) {
        // 1. Guardar en Diario Nube
        val remoteDiario = DiarioRemote(
            id = evento.id,
            jardineraId = evento.jardineraId,
            bancalId = evento.bancalId,
            fecha = evento.fecha,
            tipo = evento.tipo.name,
            titulo = evento.titulo,
            descripcion = evento.descripcion,
            fotoUrl = evento.fotoUrl
        )
        FirebaseClient.firestore.collection("diario").document(evento.id).set(remoteDiario)

        // 2. Repercusión en el Bancal (Automatización)
        if (evento.bancalId != null) {
            // Ejemplo simple: Si es Riego, actualizamos fechaUltimoRiego en Firebase
            if (evento.tipo == TipoEvento.RIEGO) {
                FirebaseClient.firestore.collection("bancales").document(evento.bancalId).update(
                    "fechaUltimoRiego" to evento.fecha
                )
            }
            // Si es Cosecha, vaciamos el bancal
            if (evento.tipo == TipoEvento.COSECHA) {
                FirebaseClient.firestore.collection("bancales").document(evento.bancalId).update(
                    "estado" to "VACIO",
                    "plantaNombre" to null
                )
            }
        }
    }

    suspend fun borrarEntrada(id: String) {
        try {
            // 1. Borrar de Firebase
            FirebaseClient.firestore.collection("diario").document(id).delete()
            // 2. Borrar Local (Feedback instantáneo)
            dao.deleteEntrada(id)
        } catch (e: Exception) {
            println("Error borrando entrada: ${e.message}")
        }
    }

    fun generateId(): String = dev.gitlive.firebase.Firebase.firestore.collection("tmp").document.id
}