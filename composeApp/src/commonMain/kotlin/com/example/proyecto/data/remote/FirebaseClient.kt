package com.example.proyecto.data.remote

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage

/**
 * Úsalo en tus Repositorios para acceder a la nube.
 * Al usar la librería GitLive, la inicialización es automática en Android (vía ContentProvider).
 */
object FirebaseClient {
    val auth = Firebase.auth
    val firestore = Firebase.firestore
    val storage = Firebase.storage
}