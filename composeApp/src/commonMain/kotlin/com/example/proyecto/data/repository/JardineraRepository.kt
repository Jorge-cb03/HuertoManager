package com.example.proyecto.data.repository

import com.example.proyecto.data.database.AppDatabase
import com.example.proyecto.data.database.entity.*
import kotlinx.coroutines.flow.first

// --- CLASES LOCALES (Sustituyen a las de la API eliminada) ---
// Las definimos aquí para mantener la compatibilidad con el resto de la app
data class PerenualImage(
    val regularUrl: String?,
    val mediumUrl: String? = null
)

data class PerenualSpecies(
    val id: Int,
    val commonName: String,
    val scientificName: List<String>,
    val defaultImage: PerenualImage?
)
// -------------------------------------------------------------

class JardineraRepository(private val db: AppDatabase) {
    private val jardineraDao = db.jardineraDao()
    private val bancalDao = db.bancalDao()
    private val productoDao = db.productoDao()
    private val diarioDao = db.entradaDiarioDao()
    private val alertDao = db.alertDao()

    // --- GESTIÓN DE ALERTAS ---
    fun getAlerts() = alertDao.getAllAlerts()
    suspend fun insertAlert(alert: AlertEntity) = alertDao.insertAlert(alert)
    suspend fun updateAlert(alert: AlertEntity) = alertDao.updateAlert(alert)
    suspend fun deleteAlert(alert: AlertEntity) = alertDao.deleteAlert(alert)

    // ===================================================================================
    // CATÁLOGO MAESTRO (LOCAL - FUENTE DE VERDAD)
    // ===================================================================================
    data class FichaCultivo(
        val id: Int,
        val nombre: String,
        val cientifico: String,
        val imagenUrl: String,
        val riegoDias: Int,
        val sol: String,
        val germinacion: String,
        val amigos: String,
        val enemigos: String,
        val consejo: String
    )

    private val catalogoMaestro = listOf(
        FichaCultivo(1, "Tomate", "Solanum lycopersicum", "https://image.tuasaude.com/media/article/cd/dd/beneficios-do-tomate_14243.jpg?width=686&height=487", 3, "Pleno Sol", "5-10 días", "Albahaca, Zanahoria", "Patata, Pepino", "Entutora la planta y quita los chupones axilares."),
        FichaCultivo(2, "Tomate Cherry", "S. lycopersicum var. cerasiforme", "https://www.infobae.com/resizer/v2/ZAVPRWEOAJDANN4D62IBYWGWBA.jpeg?auth=f9851cde38b4293eabd89f992cb7d58bb900669ba9f02a29e993cfb280e834a7&smart=true&width=1200&height=1200&quality=85", 2, "Pleno Sol", "5-8 días", "Albahaca, Ajo", "Patata", "Ideal macetas. No mojes las hojas al regar."),
        FichaCultivo(3, "Lechuga", "Lactuca sativa", "https://s1.abcstatics.com/media/bienestar/2020/09/01/lechuga-kSlD--1248x698@abc.jpg", 2, "Sombra Parcial", "4-10 días", "Fresa, Zanahoria", "Perejil", "Planta escalonada para tener siempre fresca."),
        FichaCultivo(4, "Pimiento", "Capsicum annuum", "https://corp.ametllerorigen.com/wp-content/uploads/2023/11/Blog_pebrot.jpg", 3, "Sol y Calor", "8-12 días", "Albahaca, Cebolla", "Judías", "Necesita mucho calor para germinar."),
        FichaCultivo(5, "Cebolla", "Allium cepa", "https://www.josebernad.com/wp-content/uploads/2019/07/tipos-cebollas-1024x577.jpg", 4, "Pleno Sol", "10-15 días", "Tomate, Zanahoria", "Guisantes", "Deja de regar cuando las hojas caigan para madurar."),
        FichaCultivo(6, "Zanahoria", "Daucus carota", "https://i.blogs.es/127977/carrots-2387394_1280-1-/1366_2000.jpg", 3, "Sol", "12-15 días", "Tomate, Puerro", "Eneldo", "Tierra muy suelta y sin piedras o saldrán deformes."),
        FichaCultivo(7, "Ajo", "Allium sativum", "https://scrippsamg.com/wp-content/uploads/2023/03/6_-_National_Garlic_Day_2.jpg", 5, "Pleno Sol", "10-15 días", "Fresa, Rosas", "Legumbres", "Planta el diente con la punta hacia arriba."),
        FichaCultivo(8, "Patata", "Solanum tuberosum", "https://bioky.es/wp-content/uploads/2023/12/planta-patata.jpg", 4, "Sol", "15-20 días", "Maíz, Judías", "Tomate", "Cubre con tierra la base (aporcar) conforme crezca."),
        FichaCultivo(9, "Berenjena", "Solanum melongena", "https://recetasdecocina.elmundo.es/wp-content/uploads/2022/03/berenjena.jpg", 3, "Mucho Sol", "10-15 días", "Judías", "Patata", "Consume muchos nutrientes, abona bien."),
        FichaCultivo(10, "Pepino", "Cucumis sativus", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQdjQU6-PKPgfIiUdvU5A_mlL1V6QCX2DI1zQ&s", 2, "Sol/Humedad", "3-10 días", "Maíz, Lechuga", "Patata", "Ponle una red para que trepe y los frutos no toquen suelo."),
        FichaCultivo(11, "Calabacín", "Cucurbita pepo", "https://www.frutas-hortalizas.com/img/fruites_verdures/presentacio/44.jpg", 2, "Sol", "5-10 días", "Maíz, Capuchina", "Patata", "Cosecha cuando sean pequeños (20cm)."),
        FichaCultivo(12, "Fresa", "Fragaria", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRiEqDgTImHMG_JCK0YuhyvzSaql86IlM4Saw&s", 2, "Sol/Sombra", "20-30 días", "Ajo, Espinaca", "Repollo", "Usa paja en el suelo para proteger el fruto.")
    )

    // Búsqueda usando el catálogo local
    suspend fun buscarCultivosOnline(query: String): List<PerenualSpecies> {
        val q = query.lowercase().trim()
        return catalogoMaestro
            .filter { it.nombre.lowercase().contains(q) }
            .map {
                PerenualSpecies(
                    id = it.id,
                    commonName = it.nombre,
                    scientificName = listOf(it.cientifico),
                    defaultImage = PerenualImage(it.imagenUrl, null)
                )
            }
    }

    suspend fun plantarEnBancal(bancalId: Long, localId: Int) {
        val productoLocal = productoDao.getProductoByPerenualId(localId) ?: return
        if (productoLocal.stock <= 0) return
        val ficha = catalogoMaestro.find { it.id == localId } ?: return

        bancalDao.getBancalById(bancalId)?.let { bancal ->
            bancalDao.insertOrUpdateBancal(bancal.copy(
                perenualId = localId,
                nombreCultivo = ficha.nombre,
                imagenUrl = ficha.imagenUrl,
                frecuenciaRiegoDias = ficha.riegoDias,
                necesidadSol = ficha.sol,
                fechaSiembra = System.currentTimeMillis()
            ))
            diarioDao.insertEntrada(EntradaDiarioEntity(
                bancalId = bancalId,
                tipoAccion = "SIEMBRA",
                descripcion = "Siembra: ${ficha.nombre}.\n💡 Consejo: ${ficha.consejo}",
                fecha = System.currentTimeMillis()
            ))
        }
        productoDao.updateProducto(productoLocal.copy(stock = productoLocal.stock - 1.0))
    }

    fun getFichaCompleta(id: Int): FichaCultivo? = catalogoMaestro.find { it.id == id }

    // --- MÉTODOS CRUD ESTÁNDAR ---
    fun getJardineras() = jardineraDao.getJardinerasActivas()
    fun getJardinerasArchivadas() = jardineraDao.getJardinerasArchivadas()
    fun getBancales(id: Long) = bancalDao.getBancalesByJardinera(id)
    suspend fun getBancalById(id: Long) = bancalDao.getBancalById(id)
    suspend fun setEstadoFuncionalBancal(id: Long, f: Boolean) { bancalDao.getBancalById(id)?.let { bancalDao.insertOrUpdateBancal(it.copy(esFuncional = f)) } }
    fun getProductos() = productoDao.getAllProductos()
    suspend fun getProductoById(id: Long) = productoDao.getProductoById(id)
    suspend fun insertarProducto(p: ProductoEntity) = productoDao.insertProducto(p)
    suspend fun eliminarProducto(id: Long) = productoDao.deleteProductoById(id)
    fun getTodoElHistorial() = diarioDao.getAllEntradas()
    fun getHistorialBancal(id: Long) = diarioDao.getDiarioByBancal(id)
    suspend fun insertarEntradaDiario(e: EntradaDiarioEntity) = diarioDao.insertEntrada(e)
    suspend fun registrarRiego(id: Long, l: Double) { diarioDao.insertEntrada(EntradaDiarioEntity(bancalId = id, tipoAccion = "RIEGO", descripcion = "Riego: $l L.", fecha = System.currentTimeMillis())) }
    suspend fun registrarTratamiento(id: Long, pId: Long, cant: Double, t: String) {
        val p = productoDao.getProductoById(pId) ?: return
        val nuevo = p.stock - cant
        if (nuevo <= 0) productoDao.deleteProductoById(p.id) else productoDao.updateProducto(p.copy(stock = nuevo))
        diarioDao.insertEntrada(EntradaDiarioEntity(bancalId = id, tipoAccion = t, descripcion = "$t: $cant de ${p.nombre}", fecha = System.currentTimeMillis()))
    }
    suspend fun cosecharBancal(id: Long) { bancalDao.getBancalById(id)?.let { b -> bancalDao.insertOrUpdateBancal(b.copy(perenualId = null, nombreCultivo = null, imagenUrl = null, fechaSiembra = null, frecuenciaRiegoDias = null, necesidadSol = null)); diarioDao.insertEntrada(EntradaDiarioEntity(bancalId = id, tipoAccion = "COSECHA", descripcion = "Cosechado", fecha = System.currentTimeMillis())) } }
    suspend fun crearJardineraConBancales(n: String, f: Int, c: Int) { val id = jardineraDao.insertJardinera(JardineraEntity(nombre = n, filas = f, columnas = c)); syncBancales(id, f, c) }
    suspend fun actualizarJardinera(j: JardineraEntity) { jardineraDao.updateJardinera(j); syncBancales(j.id, j.filas, j.columnas) }
    suspend fun archivarJardinera(j: JardineraEntity) = jardineraDao.updateJardinera(j.copy(estaArchivada = true))
    suspend fun desarchivarJardinera(j: JardineraEntity) = jardineraDao.updateJardinera(j.copy(estaArchivada = false))
    suspend fun regarTodaLaJardinera(jId: Long) { bancalDao.getBancalesByJardinera(jId).first().filter { it.perenualId != null }.forEach { registrarRiego(it.id, 1.0) } }
    private suspend fun syncBancales(id: Long, f: Int, c: Int) { bancalDao.deleteBancalesFueraDeRango(id, f, c); val actuales = bancalDao.getBancalesByJardinera(id).first(); for (r in 0 until f) { for (ci in 0 until c) { if (actuales.none { it.fila == r && it.columna == ci }) { bancalDao.insertOrUpdateBancal(BancalEntity(jardineraId = id, fila = r, columna = ci)) } } } }
}