package com.example.proyecto.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.proyecto.data.database.appContext // Usamos el que ya tenemos

actual object NotificationManager {
    actual fun scheduleNotification(title: String, message: String, epochSeconds: Long) {
        val intent = Intent(appContext, NotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            appContext,
            epochSeconds.toInt(), // ID único para que no se sobreescriban
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val timeInMillis = epochSeconds * 1000

        // Programar la alarma
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
    }
}