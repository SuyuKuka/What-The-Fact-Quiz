package fr.uparis.chenliu.projet.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit
import fr.uparis.chenliu.projet.ui.worker.WorkerReminder

class TimeConfigViewModel(val app: Application) : AndroidViewModel(app) {

    fun schedule() {
        val wm = WorkManager.getInstance(app)
        wm.enqueueUniquePeriodicWork(
            "daily_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            perRequest()
        )
    }

    private fun perRequest(): PeriodicWorkRequest {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
        }
        if (target.before(now)) target.add(Calendar.DAY_OF_YEAR, 1)
        val delta = target.timeInMillis - now.timeInMillis

        val notificationData = Data.Builder()
            .putString("NOTIFICATION_TYPE", "DAILY_REMIND")
            .build()

        val request = PeriodicWorkRequestBuilder<WorkerReminder>(1, TimeUnit.DAYS)
            .setInitialDelay(delta, TimeUnit.MILLISECONDS)
            .setInputData(notificationData)
            .addTag("daily_reminder")
            .build()
        return request
    }
}