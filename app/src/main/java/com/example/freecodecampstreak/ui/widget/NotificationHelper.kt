package com.example.freecodecampstreak.ui.widget

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.freecodecampstreak.R
import com.example.freecodecampstreak.ui.MainActivity

class NotificationHelper(private val context: Context) {

    private val channelId = "streak_notifications"
    private val notificationId = 1001

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Streak Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for FreeCodeCamp streak reminders"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showPendingNotification() {
        val title = "Daily Lesson Reminder"
        val message = "Don't forget to make your daily lesson!"
        showNotification(title, message)
    }

    fun showLastChanceNotification() {
        val title = "Last Chance!"
        val message = "Last chance to keep your streak!"
        showNotification(title, message)
    }

    private fun showNotification(title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = ContextCompat.getSystemService(context, NotificationManager::class.java)
        manager.notify(notificationId, notification)
    }
}