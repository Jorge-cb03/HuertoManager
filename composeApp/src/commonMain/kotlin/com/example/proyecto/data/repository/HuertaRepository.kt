package com.example.proyecto.data.repository

import com.example.proyecto.data.local.HuertaDao
import com.example.proyecto.data.local.JardineraEntity
import com.example.proyecto.data.remote.FirebaseClient
import com.example.proyecto.data.remote.model.JardineraRemote
import com.example.proyecto.domain.model.EstadoJardinera
import com.example.proyecto.domain.model.Jardinera
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HuertaRepository(
    private val dao: HuertaDao
) {
    // 1. LECTURA: Siempre desde Room (Single Source of Truth)
    // La UI solo observa esto. No sabe que existe Firebase.
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

    // 2. SINCRONIZACIÓN: Escuchar Firebase -> Guardar en Room
    // Llama a esto al iniciar la app (en el ViewModel o en el App)
    fun startSync() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 'snapshots' nos da actualizaciones en tiempo real
                FirebaseClient.firestore.collection("jardineras").snapshots.collect { querySnapshot ->
                    val listaRemota = querySnapshot.documents.map { doc ->
                        doc.data<JardineraRemote>()
                    }

                    // Convertir a Entidades de Room
                    val entidades = listaRemota.map { remote ->
                        JardineraEntity(
                            id = remote.id,
                            nombre = remote.nombre,
                            cultivo = remote.cultivo,
                            estado = remote.estado,
                            icon = remote.icon
                        )
                    }

                    // Guardar en local (Room se encargará de avisar a la UI gracias al Flow)
                    entidades.forEach { dao.insertJardinera(it) }
                }
            } catch (e: Exception) {
                println("Error en Sync: ${e.message}")
                // Aquí podrías gestionar errores silenciosos
            }
        }
    }

    // 3. ESCRITURA: App -> Firebase
    // No escribimos en Room directamente. Escribimos en la nube, la nube nos devuelve el dato en startSync, y ahí se guarda en Room.
    suspend fun crearJardinera(nombre: String) {
        val nuevaId = generateUniqueId() // O deja que Firebase genere el ID si prefieres
        val nuevaJardinera = JardineraRemote(
            id = nuevaId,
            nombre = nombre,
            estado = "VACIO",
            cultivo = null
        )

        // Escribimos en Firestore
        FirebaseClient.firestore.collection("jardineras").document(nuevaId).set(nuevaJardinera)
    }

    private fun generateUniqueId(): String = dev.gitlive.firebase.Firebase.firestore.collection("temp").document.id // Truco para ID único de Firestore o usa UUID
}