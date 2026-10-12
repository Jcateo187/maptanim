package com.maptanim.app.core.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.maptanim.app.MainActivity
import com.maptanim.app.R

object NotificationHelper {

    const val CHANNEL_FARM_ALERTS = "maptanim_farm_alerts"
    const val CHANNEL_FARM_UPDATES = "maptanim_farm_updates"

    /**
     * Initializes notification channels for Android 8.0+ (API 26+)
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                    ?: return

            // Channel 1: Farm Alerts & Reminders (High Priority)
            val alertsChannel = NotificationChannel(
                CHANNEL_FARM_ALERTS,
                "Farm Alerts & Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical alerts for irrigation, fertilization, pest detection, and harvest tasks"
                enableVibration(true)
                setShowBadge(true)
            }

            // Channel 2: Farm Updates & Weather (Default Priority)
            val updatesChannel = NotificationChannel(
                CHANNEL_FARM_UPDATES,
                "Farm Updates & Bulletins",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily farm summaries, agronomic tips, and weather advisories"
                setShowBadge(true)
            }

            notificationManager.createNotificationChannels(listOf(alertsChannel, updatesChannel))
        }
    }

    /**
     * Checks if notification permission is granted
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    /**
     * Shows a local system notification if permissions are granted
     */
    fun showNotification(
        context: Context,
        id: Int,
        title: String,
        message: String,
        channelId: String = CHANNEL_FARM_ALERTS
    ) {
        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(
                if (channelId == CHANNEL_FARM_ALERTS)
                    NotificationCompat.PRIORITY_HIGH
                else
                    NotificationCompat.PRIORITY_DEFAULT
            )
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            NotificationManagerCompat.from(context).notify(id, builder.build())
        } catch (_: SecurityException) {
            // Gracefully ignore if revoked
        }
    }
}
