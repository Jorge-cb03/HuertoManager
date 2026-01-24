package com.example.proyecto.data.repository

import com.example.proyecto.data.local.DiarioEntity // IMPORTANTE
import com.example.proyecto.data.local.HuertaDao
import com.example.proyecto.data.local.JardineraEntity
import com.example.proyecto.data.remote.FirebaseClient
import com.example.proyecto.data.remote.model.DiarioRemote // IMPORTANTE
import com.example.proyecto.data.remote.model.JardineraRemote
import com.example.proyecto.domain.model.EstadoJardinera
import dev.gitlive.firebase.firestore.firestore // Asegúrate de este import
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HuertaRepository(
    private val dao: HuertaDao
) {
    // --- LECTURA (UI) ---
    val jardineras = dao.getJardineras().map { entities ->
        entities.map { entity ->
            Jardinera(
                id = entity.id,
                nombre = entity.nombre,
                cultivo = entity.cultivo,
                estado = EstadoJardinera.valueOf(entity.estado),
                icon = entity.icon
            )
        }
    }

    val diarioGlobal = dao.getDiarioGlobal()

    // Función para ver el diario de una planta (Leemos de Room)
    fun getDiario(jardineraId: String) = dao.getDiarioPorJardinera(jardineraId)

    // --- SINCRONIZACIÓN (Motor) ---
    fun startSync() {
        CoroutineScope(Dispatchers.IO).launch {
            // 1. Escuchar JARDINERAS
            launch {
                try {
                    FirebaseClient.firestore.collection("jardineras").snapshots.collect { qs ->
                        val lista = qs.documents.map { it.data<JardineraRemote>() }
                        lista.forEach { remote ->
                            dao.insertJardinera(JardineraEntity(
                                id = remote.id,
                                nombre = remote.nombre,
                                cultivo = remote.cultivo,
                                estado = remote.estado,
                                icon = remote.icon
                            ))
                        }
                    }
                } catch (e: Exception) { println("Error Sync Jardineras: ${e.message}") }
            }

            // 2. Escuchar DIARIO (Nuevo)
            launch {
                try {
                    FirebaseClient.firestore.collection("diario").snapshots.collect { qs ->
                        val lista = qs.documents.map { it.data<DiarioRemote>() }
                        lista.forEach { remote ->
                            dao.insertEntrada(DiarioEntity(
                                id = remote.id,
                                jardineraId = remote.jardineraId,
                                fecha = remote.fecha,
                                titulo = remote.titulo,
                                descripcion = remote.descripcion,
                                tipo = remote.tipo,
                                fotoUrl = remote.fotoUrl
                            ))
                        }
                    }
                } catch (e: Exception) { println("Error Sync Diario: ${e.message}") }
            }


            launch {
                try {
                    FirebaseClient.firestore.collection("productos").snapshots.collect { qs ->
                        val lista = qs.documents.map { it.data<com.example.proyecto.data.remote.model.ProductoRemote>() }
                        lista.forEach { remote ->
                            dao.insertProducto(com.example.proyecto.data.local.ProductoEntity(
                                id = remote.id,
                                nombre = remote.nombre,
                                tipo = remote.tipo,
                                cantidad = remote.cantidad,
                                descripcion = remote.descripcion
                            ))
                        }
                    }
                } catch (e: Exception) { println("Error Sync Productos: ${e.message}") }
            }

        }
    }

    // --- ESCRITURA (Acciones) ---
    suspend fun crearJardinera(nombre: String) {
        val id = generateId()
        val remote = JardineraRemote(id = id, nombre = nombre)
        FirebaseClient.firestore.collection("jardineras").document(id).set(remote)
    }

    // Nueva función para crear entradas
    suspend fun crearEntrada(jardineraId: String, titulo: String, descripcion: String, tipo: String) {
        val id = generateId()
        val remote = DiarioRemote(
            id = id,
            jardineraId = jardineraId,
            fecha = kotlinx.datetime.Clock.System.now().toEpochMilliseconds(), // O System.currentTimeMillis()
            titulo = titulo,
            descripcion = descripcion,
            tipo = tipo
        )
        FirebaseClient.firestore.collection("diario").document(id).set(remote)
    }

    private fun generateId(): String = dev.gitlive.firebase.Firebase.firestore.collection("tmp").document.id

    suspend fun borrarEntrada(id: String) {
        // Borrar de Firebase
        FirebaseClient.firestore.collection("diario").document(id).delete()
        // Borrar de local (opcional, pero ayuda a que la UI reaccione instantáneo si la sync tarda)
        // dao.deleteEntrada(id) // Si implementas delete en el DAO
    }

    // --- INVENTARIO (Lectura) ---
    val productos = dao.getProductos().map { entities ->
        entities.map { entity ->
            // Mapeo rápido a tu modelo de UI (o crea uno de dominio limpio si prefieres)
            // Aquí usaremos una clase simple interna o reutilizaremos la de UI por velocidad
            com.example.proyecto.ui.products.InventoryItem(
                id = entity.id,
                name = entity.nombre,
                type = try { com.example.proyecto.ui.products.ProductType.valueOf(entity.tipo) } catch (e: Exception) { com.example.proyecto.ui.products.ProductType.OTHER },
                quantity = entity.cantidad,
                description = entity.descripcion
            )
        }
    }

    // --- INVENTARIO (Sync Engine) ---
    // Añade esto dentro de tu función startSync(), en un nuevo launch:

    // --- INVENTARIO (Escritura) ---
    suspend fun crearProducto(nombre: String, tipo: String, cantidad: String, desc: String) {
        val id = generateId()
        val remote = com.example.proyecto.data.remote.model.ProductoRemote(
            id = id, nombre = nombre, tipo = tipo, cantidad = cantidad, descripcion = desc
        )
        FirebaseClient.firestore.collection("productos").document(id).set(remote)
    }

    suspend fun borrarProducto(id: String) {
        FirebaseClient.firestore.collection("productos").document(id).delete()
        dao.deleteProducto(id)
    }
}