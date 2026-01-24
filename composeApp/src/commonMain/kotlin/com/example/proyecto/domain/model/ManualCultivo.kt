package com.example.proyecto.domain.model

object ManualCultivo {
    // Lista negra: "Si plantas A, no plantes B cerca"
    private val enemigos = mapOf(
        "Tomate" to listOf("Patata", "Pepino", "Hinojo", "Col"),
        "Patata" to listOf("Tomate", "Calabaza", "Girasol"),
        "Lechuga" to listOf("Perejil", "Apio"),
        "Zanahoria" to listOf("Eneldo", "Anís"),
        "Cebolla" to listOf("Judía", "Guisante", "Haba"),
        "Ajo" to listOf("Judía", "Guisante", "Haba"),
        "Judía" to listOf("Cebolla", "Ajo", "Pimiento"),
        "Pimiento" to listOf("Judía", "Col")
    )

    // Lista blanca: "Estos se ayudan entre sí"
    private val amigos = mapOf(
        "Tomate" to listOf("Albahaca", "Zanahoria"),
        "Zanahoria" to listOf("Tomate", "Lechuga", "Cebolla"),
        "Lechuga" to listOf("Fresa", "Zanahoria", "Rábano"),
        "Judía" to listOf("Maíz", "Patata", "Fresa"),
        "Cebolla" to listOf("Zanahoria", "Remolacha", "Tomate")
    )

    fun getRelacion(plantaA: String, plantaB: String): Relacion {
        val a = plantaA.trim()
        val b = plantaB.trim()

        if (enemigos[a]?.any { it.equals(b, ignoreCase = true) } == true ||
            enemigos[b]?.any { it.equals(a, ignoreCase = true) } == true) {
            return Relacion.ENEMIGOS
        }
        if (amigos[a]?.any { it.equals(b, ignoreCase = true) } == true ||
            amigos[b]?.any { it.equals(a, ignoreCase = true) } == true) {
            return Relacion.AMIGOS
        }
        return Relacion.NEUTRAL
    }
}

enum class Relacion { AMIGOS, ENEMIGOS, NEUTRAL }