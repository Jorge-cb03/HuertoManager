package com.example.proyecto.data.repository

import com.example.proyecto.data.remote.FirebaseClient
import dev.gitlive.firebase.auth.FirebaseUser

class AuthRepository {
    private val auth = FirebaseClient.auth

    // Devuelve el usuario si ya está logueado, o null si no
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    // Login Anónimo (Invitado)
    suspend fun loginAnonymously(): Result<Unit> {
        return try {
            auth.signInAnonymously()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // Cerrar sesión
    suspend fun logout() {
        auth.signOut()
    }
}