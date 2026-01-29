package com.example.proyecto.data.repository

import com.example.proyecto.data.api.OpenFarmService
import com.example.proyecto.data.api.model.OpenFarmResponse
import com.example.proyecto.data.database.AppDatabase
import com.example.proyecto.data.database.entity.*
import io.ktor.client.call.body
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class JardineraRepository(private val db: AppDatabase, private val api: OpenFarmService) {
    private val jardineraDao = db.jardineraDao()
    private val bancalDao = db.bancalDao()
    private val productoDao = db.productoDao()
    private val diarioDao = db.entradaDiarioDao()

    fun getJardineras(): Flow<List<JardineraEntity>> = jardineraDao.getAllJardineras()
    fun getBancales(id: Long) = bancalDao.getBancalesByJardinera(id)
    suspend fun getBancalById(id: Long) = bancalDao.getBancalById(id)
    fun getHistorialBancal(id: Long) = diarioDao.getDiarioByBancal(id)

    suspend fun plantarEnBancal(bancalId: Long, cropSlug: String) {
        // 1. Buscamos el producto en el inventario local por el slug
        val productoLocal = productoDao.getProductoBySlug(cropSlug)

        if (productoLocal != null && productoLocal.stock > 0) {
            // 2. RESTAMOS STOCK REAL
            productoDao.updateProducto(productoLocal.copy(stock = productoLocal.stock - 1))

            // 3. Intentamos pillar la foto de la API
            val apiResult = runCatching {
                val response = api.buscarCultivo(cropSlug)
                response.body<OpenFarmResponse>().data.firstOrNull()
            }.getOrNull()

            val nombreParaElBancal = productoLocal.nombre // "Tomate Cherry"
            val imagenUrl = apiResult?.attributes?.mainImagePath ?: ""

            // 4. Actualizamos el bancal con el nombre real de nuestro producto
            bancalDao.getBancalById(bancalId)?.let { bancal ->
                bancalDao.insertOrUpdateBancal(bancal.copy(
                    cultivoSlug = cropSlug,
                    nombreCultivo = nombreParaElBancal,
                    imagenUrl = imagenUrl,
                    fechaSiembra = System.currentTimeMillis()
                ))

                // 5. REGISTRO EN EL DIARIO (Aparecerá en el historial de detalle)
                diarioDao.insertEntrada(EntradaDiarioEntity(
                    bancalId = bancalId,
                    tipoAccion = "SIEMBRA",
                    descripcion = "Se ha plantado $nombreParaElBancal. Stock restante: ${productoLocal.stock - 1}",
                    fecha = System.currentTimeMillis()
                ))
            }
        }
    }

    suspend fun inicializarDatosPrueba() {
        if (jardineraDao.getAllJardineras().first().isEmpty()) {
            crearJardineraConBancales("Mi Huerto Principal", 4, 2)
            productoDao.insertProducto(ProductoEntity(nombre = "Tomate Cherry", categoria = "SEMILLA", stock = 10, openFarmSlug = "tomato"))
        }
    }

    suspend fun crearJardineraConBancales(nombre: String, filas: Int, columnas: Int) {
        val idJ = jardineraDao.insertJardinera(JardineraEntity(nombre = nombre, filas = filas, columnas = columnas))
        for (f in 0 until filas) for (c in 0 until columnas) {
            bancalDao.insertOrUpdateBancal(BancalEntity(jardineraId = idJ, fila = f, columna = c))
        }
    }
    fun getTodoElHistorial(): Flow<List<EntradaDiarioEntity>> = diarioDao.getAllEntradas()

    fun getProductos(): Flow<List<ProductoEntity>> = productoDao.getAllProductos()

    suspend fun getProductoById(id: Long) = productoDao.getProductoById(id)

    suspend fun insertarEntradaDiario(entrada: EntradaDiarioEntity) = diarioDao.insertEntrada(entrada)
    suspend fun getEntradaDiarioById(id: Long): EntradaDiarioEntity? = diarioDao.getEntradaById(id)
    suspend fun eliminarEntradaDiario(id: Long) {
        diarioDao.deleteById(id)}
}