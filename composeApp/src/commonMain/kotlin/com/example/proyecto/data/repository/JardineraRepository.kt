package com.example.proyecto.data.repository

import com.example.proyecto.data.api.OpenFarmService
import com.example.proyecto.data.api.model.OpenFarmResponse
import com.example.proyecto.data.database.AppDatabase
import com.example.proyecto.data.database.entity.*
import io.ktor.client.call.body
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class JardineraRepository(private val db: AppDatabase, private val api: OpenFarmService) {
    // 1. Definición de DAOs
    private val jardineraDao = db.jardineraDao()
    private val bancalDao = db.bancalDao()
    private val productoDao = db.productoDao()
    private val diarioDao = db.entradaDiarioDao()

    // 2. Consultas básicas para la UI
    fun getJardineras(): Flow<List<JardineraEntity>> = jardineraDao.getAllJardineras()

    fun getBancales(jardineraId: Long): Flow<List<BancalEntity>> =
        bancalDao.getBancalesByJardinera(jardineraId)

    /**
     * Crea una jardinera y genera automáticamente sus bancales vacíos.
     */
    suspend fun crearJardineraConBancales(nombre: String, filas: Int, columnas: Int) {
        val id = jardineraDao.insertJardinera(
            JardineraEntity(nombre = nombre, filas = filas, columnas = columnas)
        )

        for (f in 0 until filas) {
            for (c in 0 until columnas) {
                // Usamos insertOrUpdateBancal para asegurar la persistencia inicial
                bancalDao.insertOrUpdateBancal(
                    BancalEntity(jardineraId = id, fila = f, columna = c)
                )
            }
        }
    }

    /**
     * Lógica principal: consulta la API, actualiza el bancal y crea una entrada en el diario.
     */
    suspend fun plantarEnBancal(bancalId: Long, cropSlug: String) {
        // Consultamos la API de OpenFarm manejando la lista de resultados
        val result = runCatching {
            val response = api.buscarCultivo(cropSlug)
            val body = response.body<OpenFarmResponse>()
            body.data.firstOrNull() // Extraemos el primer cultivo de la búsqueda
        }

        val crop = result.getOrNull()
        // Mapeamos los atributos del backend, incluyendo la imagen
        val nombre = crop?.attributes?.name ?: "Cultivo Desconocido"
        val imagen = crop?.attributes?.mainImagePath ?: ""

        val bancal = bancalDao.getBancalById(bancalId)
        bancal?.let {
            // Actualizamos la base de datos con la info de la API
            bancalDao.insertOrUpdateBancal(it.copy(
                cultivoSlug = cropSlug,
                nombreCultivo = nombre,
                imagenUrl = imagen,
                fechaSiembra = System.currentTimeMillis()
            ))

            // Generamos automáticamente el registro en el diario
            diarioDao.insertEntrada(EntradaDiarioEntity(
                bancalId = bancalId,
                tipoAccion = "SIEMBRA",
                descripcion = "Se ha plantado $nombre usando stock del inventario."
            ))
        }
    }

    /**
     * Precarga de datos para que la app no aparezca vacía al principio.
     */
    suspend fun inicializarDatosPrueba() {
        val jardineras = jardineraDao.getAllJardineras().first()

        if (jardineras.isEmpty()) {
            // Si no hay nada, creamos la estructura base
            crearJardineraConBancales("Mi Huerto Principal", 4, 2)

            // Añadimos stock inicial de semillas
            productoDao.insertProducto(
                ProductoEntity(
                    nombre = "Tomate Cherry",
                    categoria = "SEMILLA",
                    stock = 10,
                    openFarmSlug = "tomato"
                )
            )
        }
    }

    // Añade esto a JardineraRepository.kt si no lo tienes:
    suspend fun getBancalById(id: Long): BancalEntity? = bancalDao.getBancalById(id)

    fun getHistorialPorBancal(bancalId: Long) = diarioDao.getEntradasByBancal(bancalId)
}