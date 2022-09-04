package elfak.mosis.iwalk.services.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import elfak.mosis.iwalk.MainActivity
import elfak.mosis.iwalk.R

class NotificationHelpers {
    companion object {

        private const val NOTIFICATION_CHANNEL_ID = "channel1"
        private const val NOTIFICATION_CHANNEL_POST_ID = "channel2"
        private const val NOTIFICATION_CHANNEL_NAME = "nearby"
        private const val NOTIFICATION_CHANNEL_POST_NAME = "nearby2"
        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_POST_ID = 2
        private const val PENDING_INTENT_ID = 1
        private const val PENDING_INTENT_POST_ID = 2


        fun pushNotification(context: Context, title: String, content: String) {
            val notificationManager : NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel: NotificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)

            notificationManager.createNotificationChannel(channel)

            val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.purple_500))
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .setContentIntent(createPendingIntent(context))
                .setAutoCancel(true)

            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }

        fun pushNotificationForPost(context: Context, title: String, content: String) {
            val notificationManager : NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel: NotificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_POST_ID, NOTIFICATION_CHANNEL_POST_NAME, NotificationManager.IMPORTANCE_HIGH)

            notificationManager.createNotificationChannel(channel)

            val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_POST_ID)
                .setColor(ContextCompat.getColor(context, R.color.purple_700))
                .setSmallIcon(R.drawable.ic_my_pets)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .setContentIntent(createPendingIntentPost(context))
                .setAutoCancel(true)

            notificationManager.notify(NOTIFICATION_POST_ID, builder.build())
        }


        private fun createPendingIntent(context: Context) : PendingIntent {
            val startActivityIntent: Intent = Intent(context, MainActivity::class.java)
            return PendingIntent.getActivity(context, PENDING_INTENT_ID,
                startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        }

        private fun createPendingIntentPost(context: Context) : PendingIntent {
            val startActivityIntent: Intent = Intent(context, MainActivity::class.java)
            return PendingIntent.getActivity(context, PENDING_INTENT_POST_ID,
                startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        }
    }
}