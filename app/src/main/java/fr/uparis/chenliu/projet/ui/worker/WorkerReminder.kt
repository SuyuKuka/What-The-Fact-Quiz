package fr.uparis.chenliu.projet.ui.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import fr.uparis.chenliu.projet.R
import fr.uparis.chenliu.projet.ui.CHANNEL_ID
import fr.uparis.chenliu.projet.ui.MainActivity

class WorkerReminder(val context: Context, params: WorkerParameters): Worker(context, params) {
    override fun doWork(): Result {

        val notificationType = inputData.getString("NOTIFICATION_TYPE")

        val title: String
        val text: String

        when (notificationType) {
            "FULL_ENERGY" -> {
                title = "Énergie Maximale‼️"
                text = "Votre jauge est complète ! Ne perdez pas d'énergie, venez jouer maintenant."
            }
            "DAILY_REMIND" -> {
                title = "C'est l'heure de l'aventure⚔️"
                text = "Connectez-vous et enrechissez vos connaissances."
            }
            else -> {
                title = "Nouvelle partie disponible !"
                text = "1 point d'énergie a été rechargé⚡. Lancez immédiatement votre session."
            }
        }

        createNotification(
            context = context,
            title = title,
            text = text
        )
        return Result.success()
    }
}

fun createNotification(context: Context, title: String, text: String) {
    val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    val intent= Intent(context, MainActivity::class.java)
    val pending= PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)
    val builder =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pending)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    val notification = builder.build()
    notificationManager.notify(42, notification)
}