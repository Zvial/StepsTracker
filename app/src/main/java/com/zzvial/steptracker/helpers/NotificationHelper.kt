package com.zzvial.steptracker.helpers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import timber.log.Timber

class NotificationHelper(
    private var appContext: Context,
    var channelID: String,
    var channelName: String) {

    private val notificationManager by lazy { appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    private var notificationChannel: NotificationChannel? = null
    var notificationID: Int? = null

    init {
        createNotificationChannel()
    }

    fun createServiceNotification(
        notifID: Int,
        smallIcon: Int,
        title: String,
        text: String? = null,
        largeIcon: Int? = null,
        priority: Int = androidx.core.app.NotificationCompat.PRIORITY_LOW
    ) =
        prepareNotificationBuilder(notifID, smallIcon, title, text, largeIcon, priority)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

    fun notify(notification: Notification) {
        checkNotificationID()
        notificationManager.notify(notificationID!!, notification)
    }

    fun unNotify() {
        checkNotificationID()
        notificationManager.cancel(notificationID!!)
    }

    private fun checkNotificationID() {
        if (notificationID == null)
            throw Exception("Не указан notification ID. Отправка notify невозможна")
    }

    private fun prepareNotificationBuilder(
        notifID: Int,
        smallIcon: Int,
        title: String,
        text: String? = null,
        largeIcon: Int? = null,
        priority: Int = androidx.core.app.NotificationCompat.PRIORITY_LOW
    ) =
        androidx.core.app.NotificationCompat.Builder(appContext, channelID)
            .setSmallIcon(smallIcon)
            .setContentTitle(title).apply {
                notificationID = notifID

                if (text != null) setContentText(text)
                if (largeIcon != null) setLargeIcon(
                    BitmapFactory.decodeResource(
                        appContext.resources,
                        largeIcon
                    )
                )
                setPriority(priority)
            }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Timber.d("notification channel not created, channelID = ''")
            channelID = ""
            return
        }

        notificationChannel = notificationManager.getNotificationChannel(channelID) ?: NotificationChannel(
            channelID,
            channelName,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            lightColor = Color.BLUE
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            enableVibration(false)
        }

        notificationManager.createNotificationChannel(notificationChannel!!)

        Timber.d("notification channel not created, channelID = $channelID")
    }

    companion object {
        const val channelStepDetectorID = "com.zzvial.sensor_observer_service"
        const val channelStepDetectorName = "Отображение шагов"
    }
}