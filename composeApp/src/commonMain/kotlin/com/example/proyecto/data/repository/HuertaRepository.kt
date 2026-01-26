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
import kotlinx.datetime.Clock

class HuertaRepository(private val dao: HuertaDao) {
    init { startSync() }

    // LECTURAS
    // Mapeamos las entidades locales a modelos de dominio
    val jardineras = dao.getJardineras().map { l -> l.map { Jardinera(it.id, it.nombre, it.filas, it.columnas, emptyList()) } }
    val bancalesGlobales = dao.getAllBancales().map { l -> l.map { mapBancalEntity(it) } }

    // CONSULTAS
    fun getHistorial() = dao.getHistorial(Clock.System.now().toEpochMilliseconds()).map { mapDiario(it) }
    fun getAlertas() = dao.getAlertas(Clock.System.now().toEpochMilliseconds()).map { mapDiario(it) }

    val productos = dao.getProductos().map { l -> l.map { Producto(it.id, it.nombre, it.tipo, it.cantidad, it.descripcion, it.icon) } }

    fun getBancalById(id: String) = dao.getBancalById(id).map { it?.let { mapBancalEntity(it) } }
    fun getDiarioPorBancal(bancalId: String) = dao.getDiarioPorBancal(bancalId).map { mapDiario(it) }

    // MAPPERS
    private fun mapBancalEntity(entity: BancalEntity): Bancal {
        return Bancal(
            id = entity.id,
            jardineraId = entity.jardineraId,
            indice = entity.indice,
            estado = try { EstadoBancal.valueOf(entity.estado) } catch (e: Exception) { EstadoBancal.VACIO },
            fechaSiembra = entity.fechaSiembra,
            fechaUltimoRiego = entity.fechaUltimoRiego,
            fechaUltimoAbono = entity.fechaUltimoAbono,
            planta = if (entity.plantaNombre != null) {
                // AHORA ESTO FUNCIONARÁ PORQUE Planta ACEPTA STRING EN EL ÚLTIMO PARÁMETRO
                Planta(
                    nombre = entity.plantaNombre,
                    variedad = entity.plantaVariedad ?: "",
                    tipo = try { TipoCultivo.valueOf(entity.plantaTipo ?: "OTRO") } catch (e: Exception) { TipoCultivo.OTRO },
                    imagenRes = entity.plantaIcono ?: "plant_default"
                )
            } else null
        )
    }

    private fun mapDiario(entities: List<DiarioEntity>) = entities.map { EntradaDiario(it.id, it.jardineraId, it.bancalId, it.fecha, try { TipoEvento.valueOf(it.tipo) } catch (e: Exception) { TipoEvento.NOTA }, it.titulo, it.descripcion, it.fotoUrl) }

    // --- ACCIONES ---

    suspend fun crearJardinera(nombre: String, filas: Int, columnas: Int) {
        try {
            val jardineraId = generateId()
            dao.insertJardinera(JardineraEntity(jardineraId, nombre, filas, columnas, "default"))

            for (i in 0 until (filas * columnas)) {
                val bancalId = generateId()
                dao.insertBancal(BancalEntity(bancalId, jardineraId, i, "VACIO", null, null, null, null, null, null, null))
                CoroutineScope(Dispatchers.IO).launch {
                    try { FirebaseClient.firestore.collection("bancales").document(bancalId).set(BancalRemote(bancalId, jardineraId, i, "VACIO")) } catch (e: Exception) { }
                }
            }
            FirebaseClient.firestore.collection("jardineras").document(jardineraId).set(JardineraRemote(jardineraId, nombre, filas, columnas))
        } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun redimensionarJardinera(jardineraId: String, filasActuales: Int, columnas: Int, nuevasFilas: Int) {
        if (nuevasFilas <= filasActuales) return
        try {
            FirebaseClient.firestore.collection("jardineras").document(jardineraId).update("filas" to nuevasFilas)
            val totalActual = filasActuales * columnas
            val totalNuevo = nuevasFilas * columnas
            for (i in totalActual until totalNuevo) {
                val bancalId = generateId()
                dao.insertBancal(BancalEntity(bancalId, jardineraId, i, "VACIO", null, null, null, null, null, null, null))
                FirebaseClient.firestore.collection("bancales").document(bancalId).set(BancalRemote(bancalId, jardineraId, i, "VACIO"))
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun borrarJardinera(id: String) {
        dao.deleteJardinera(id)
        try { FirebaseClient.firestore.collection("jardineras").document(id).delete() } catch (e: Exception) { }
    }

    suspend fun sembrarBancal(bancalId: String, producto: Producto) {
        try {
            val fechaActual = Clock.System.now().toEpochMilliseconds()
            if (producto.tipo.equals("SEMILLA", ignoreCase = true)) {
                val stock = producto.cantidad.toIntOrNull() ?: 0
                if (stock > 0) {
                    val nuevoStock = (stock - 1).toString()
                    dao.updateProductoStock(producto.id, nuevoStock)
                    FirebaseClient.firestore.collection("productos").document(producto.id).update("cantidad" to nuevoStock)
                }
            }
            dao.updateBancalSiembra(bancalId, "OCUPADO", producto.nombre, "Estándar", producto.tipo, fechaActual)
            val updates = mapOf("estado" to "OCUPADO", "plantaNombre" to producto.nombre, "plantaVariedad" to "Estándar", "plantaTipo" to producto.tipo, "fechaSiembra" to fechaActual, "fechaUltimoRiego" to fechaActual)
            FirebaseClient.firestore.collection("bancales").document(bancalId).update(updates)
            registrarEvento(EntradaDiario(generateId(), "auto", bancalId, fechaActual, TipoEvento.SIEMBRA, "Siembra de ${producto.nombre}", "Automático", null))
        } catch (e: Exception) { e.printStackTrace() }
    }

    suspend fun limpiarBancal(bancalId: String) {
        dao.updateBancalLimpieza(bancalId)
        FirebaseClient.firestore.collection("bancales").document(bancalId).update(mapOf("estado" to "VACIO", "plantaNombre" to null))
        registrarEvento(EntradaDiario(generateId(), "auto", bancalId, Clock.System.now().toEpochMilliseconds(), TipoEvento.COSECHA, "Cosecha", "Automático", null))
    }

    suspend fun regarBancal(bancalId: String, nombrePlanta: String) {
        val fechaActual = Clock.System.now().toEpochMilliseconds()
        FirebaseClient.firestore.collection("bancales").document(bancalId).update("fechaUltimoRiego" to fechaActual)
        registrarEvento(EntradaDiario(generateId(), "auto", bancalId, fechaActual, TipoEvento.RIEGO, "Riego de $nombrePlanta", "Manual", null))
    }

    suspend fun registrarEvento(evento: EntradaDiario) {
        dao.insertEntrada(DiarioEntity(evento.id, evento.jardineraId, evento.bancalId, evento.fecha, evento.tipo.name, evento.titulo, evento.descripcion, evento.fotoUrl))
        FirebaseClient.firestore.collection("diario").document(evento.id).set(DiarioRemote(evento.id, evento.jardineraId, evento.bancalId, evento.fecha, evento.tipo.name, evento.titulo, evento.descripcion, evento.fotoUrl))
    }

    suspend fun crearProducto(nombre: String, tipo: String, cantidad: String, descripcion: String) {
        val id = generateId()
        dao.insertProducto(ProductoEntity(id, nombre, tipo, cantidad, descripcion, "default"))
        FirebaseClient.firestore.collection("productos").document(id).set(ProductoRemote(id, nombre, tipo, cantidad, descripcion, "default"))
    }

    suspend fun borrarProducto(id: String) {
        dao.deleteProducto(id)
        try { FirebaseClient.firestore.collection("productos").document(id).delete() } catch (e: Exception) { }
    }

    suspend fun borrarEntrada(id: String) {
        dao.deleteEntrada(id)
        try { FirebaseClient.firestore.collection("diario").document(id).delete() } catch (e: Exception) { }
    }

    suspend fun renombrarJardinera(id: String, nombre: String) {
        dao.updateNombreJardinera(id, nombre)
        try { FirebaseClient.firestore.collection("jardineras").document(id).update("nombre" to nombre) } catch (e: Exception) { }
    }

    fun generateId(): String = dev.gitlive.firebase.Firebase.firestore.collection("tmp").document.id

    fun startSync() {
        CoroutineScope(Dispatchers.IO).launch {
            launch { runCatching { FirebaseClient.firestore.collection("jardineras").snapshots.collect { qs -> qs.documents.map { it.data<JardineraRemote>() }.forEach { dao.insertJardinera(JardineraEntity(it.id, it.nombre, it.filas, it.columnas, it.icon)) } } } }
            launch { runCatching { FirebaseClient.firestore.collection("bancales").snapshots.collect { qs -> qs.documents.map { it.data<BancalRemote>() }.forEach { dao.insertBancal(BancalEntity(it.id, it.jardineraId, it.indice, it.estado, it.fechaSiembra, it.fechaUltimoRiego, it.fechaUltimoAbono, it.plantaNombre, it.plantaVariedad, it.plantaTipo, it.plantaIcono)) } } } }
            launch { runCatching { FirebaseClient.firestore.collection("diario").snapshots.collect { qs -> qs.documents.map { it.data<DiarioRemote>() }.forEach { dao.insertEntrada(DiarioEntity(it.id, it.jardineraId, it.bancalId, it.fecha, it.tipo, it.titulo, it.descripcion, it.fotoUrl)) } } } }
            launch { runCatching { FirebaseClient.firestore.collection("productos").snapshots.collect { qs -> qs.documents.map { it.data<ProductoRemote>() }.forEach { dao.insertProducto(ProductoEntity(it.id, it.nombre, it.tipo, it.cantidad, it.descripcion, it.icon)) } } } }
        }
    }
}