package com.example.food_test.firebase
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.food_test.R
import com.example.food_test.view.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


const val channelId = "notifications_channel"
const val channelName = "com.example.food_test.firebase"

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseMessagingService : FirebaseMessagingService() {
    private lateinit var builder: NotificationCompat.Builder

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.notification != null) {
            generateNotification(
                remoteMessage.notification!!.title!!,
                remoteMessage.notification!!.body!!,
            )
        }
    }

    @SuppressLint("RemoteViewLayout")
    private fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteViews =
            RemoteViews("com.example.food_test.firebase", R.layout.firebase_noti_layout)
        remoteViews.setTextViewText(R.id.noti_title, title)
        remoteViews.setTextViewText(R.id.noti_message, message)
        remoteViews.setImageViewResource(R.id.noti_img, R.drawable.img_4)
        return remoteViews
    }

    @SuppressLint("UnspecifiedImmutableFlag", "ObsoleteSdkInt", "WrongConstant")
    fun generateNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)

        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.img_4)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setPriority(10)
            .setSound(uri)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setContentIntent(pendingIntent)
        builder = builder.setContent(getRemoteView(title, message))

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_MAX)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(0, builder.build())
    }
}