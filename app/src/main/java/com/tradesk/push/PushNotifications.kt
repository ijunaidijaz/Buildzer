package com.tradesk.push

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.tradesk.BuildConfig
import com.tradesk.R
import com.tradesk.appCode.MainActivity
import com.tradesk.appCode.jobsModule.jobDetailModule.jobChatModule.ChatActivity
import com.tradesk.utils.PreferenceConstants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class PushNotifications : FirebaseMessagingService() {
    private val TAG = PushNotifications::class.java.simpleName

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.from)
        Log.e("NOTIFICATION_DATA", "${remoteMessage.data}")
        if (remoteMessage.data != null) sendNotification(remoteMessage.data)
    }

    private fun sendNotification(data: Map<String, String?>) {
        val pref =
            getSharedPreferences(PreferenceConstants.SharedPrefenceName, Context.MODE_PRIVATE)
//        if (pref.getBool(AppConstants.IS_LOGIN).not()) return
        var title: String = getString(R.string.app_name)
        var message: String = ""
        val type = data["type"]
        if (data.containsKey("title")) title =
            (if (data["title"] == "") getString(R.string.app_name) else data["title"]
                ?: "") else title = getString(R.string.app_name)
        if (data.containsKey("message")) message = data["message"] ?: ""
        if (data.containsKey("body")) message = data["body"] ?: ""
/* notificationType:"verificationActionByAdmin",
                   isVerified:"0"
                 }
                    notificationType:"blockActionByAdmin",
                    isBlocked:"0"*/
        var pendingIntent: PendingIntent? = null
        pendingIntent =
            if (type == "text" || type == "image") {
                val jobTitle =
                    data.get("body").toString().substringAfterLast("new message in").trim()
                val intent = Intent(this, ChatActivity::class.java)
                    .putExtra("job_id", data["job_id"])
                    .putExtra("receiver_id", data["receiver"])
                    .putExtra("sales_id", data["sales_id"])
                    .putExtra("job_title", jobTitle)
                PendingIntent.getActivities(
                    this,
                    0,
                    arrayOf(Intent(this, MainActivity::class.java), intent),
                    PendingIntent.FLAG_ONE_SHOT
                )
            } else {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .putExtra("notification", true)
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            }

        if (ChatActivity.activeJobId != data["job_id"]) showNotification(
            title,
            message,
            pendingIntent
        )
    }

    fun showNotification(title: String, message: String, pendingIntent: PendingIntent) {

        val channelId = BuildConfig.APPLICATION_ID
//val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round))
            .setColor(ContextCompat.getColor(this, R.color.white_color))
            .setContentTitle(title).setContentText(message)
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message)).setAutoCancel(true)
//.setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

// Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel${getString(R.string.app_name)}",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

}