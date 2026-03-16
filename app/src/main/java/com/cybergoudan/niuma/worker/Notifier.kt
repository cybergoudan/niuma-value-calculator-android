package com.cybergoudan.niuma.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.cybergoudan.niuma.R

object Notifier {
  private const val CHANNEL_ID = "niuma_alerts"

  fun ensureChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      val ch = NotificationChannel(
        CHANNEL_ID,
        "牛马提醒",
        NotificationManager.IMPORTANCE_DEFAULT
      )
      ch.description = "娱乐成本与阈值提醒"
      nm.createNotificationChannel(ch)
    }
  }

  fun notify(context: Context, title: String, body: String) {
    ensureChannel(context)
    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val notif = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(android.R.drawable.ic_dialog_info)
      .setContentTitle(title)
      .setContentText(body)
      .setStyle(NotificationCompat.BigTextStyle().bigText(body))
      .setAutoCancel(true)
      .build()

    nm.notify(1001, notif)
  }
}
