package de.dertyp7214.rboardthememanager.services

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import de.dertyp7214.rboardthememanager.BuildConfig
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.screens.MainActivity
import org.json.JSONObject

class MessagingService : FirebaseMessagingService() {
    private val notificationId = 133769
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        handleMessage(remoteMessage)
    }

    private fun handleMessage(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        val notification = remoteMessage.notification
        val notificationData = data["notification"]
        when (data["type"]) {
            "update" -> {
                if ((data["version"]?.toInt() ?: Int.MAX_VALUE) > BuildConfig.VERSION_CODE) {
                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra("update", true)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }.let {
                        PendingIntent.getActivity(
                            this,
                            0,
                            it,
                            PendingIntent.FLAG_IMMUTABLE
                        )
                    }
                    showNotification(Notification.parse(notificationData), intent)
                }
            }
            "announcement" -> {
                if (notification != null) showNotification(Notification(notification))
                else showNotification(Notification.parse(notificationData))
            }
        }
    }

    private fun showNotification(
        notification: Notification,
        intent: PendingIntent? = null
    ) {
        val builder =
            NotificationCompat.Builder(
                this,
                getString(R.string.default_notification_channel_id)
            )
                .apply {
                    setContentTitle(notification.title)
                    setContentText(notification.body)
                    setContentIntent(intent)
                    setSmallIcon(R.drawable.ic_notification)
                    setAutoCancel(true)
                    priority = NotificationCompat.PRIORITY_DEFAULT
                }
        NotificationManagerCompat.from(this).run {
            notify(notificationId, builder.build())
        }
    }

    private class Notification(val title: String, val body: String) {
        constructor(notification: RemoteMessage.Notification?) : this(
            notification?.title ?: "",
            notification?.body ?: ""
        )

        companion object {
            fun parse(json: String?): Notification {
                return try {
                    JSONObject(json!!).let {
                        Notification(it.getString("title"), it.getString("body"))
                    }
                } catch (e: Exception) {
                    Notification("", "")
                }
            }
        }
    }
} 