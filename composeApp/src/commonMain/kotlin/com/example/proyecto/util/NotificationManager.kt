package com.example.proyecto.util

object NotificationManager {
    fun scheduleNotification(title: String, message: String, triggerAtEpochSeconds: Long) {
        // TODO: Implementar notificaciones locales reales con KMPNotifier o similar
        println("🔔 NOTIFICACIÓN PROGRAMADA:")
        println("   Título: $title")
        println("   Mensaje: $message")
        println("   Cuándo (Epoch): $triggerAtEpochSeconds")
    }

    fun cancelNotification(id: Long) {
        println("🔕 Notificación cancelada: $id")
    }
}