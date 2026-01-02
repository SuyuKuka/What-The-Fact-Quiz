package fr.uparis.chenliu.projet.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit
import fr.uparis.chenliu.projet.data.store.TimeConfig
import fr.uparis.chenliu.projet.ui.worker.WorkerReminder

class TimeConfigViewModel(val app: Application) : AndroidViewModel(app) {

    fun schedule(config: TimeConfig) {
        val wm = WorkManager.getInstance(app)
        wm.cancelAllWork()
        wm.enqueue(perRequest(config.hMorning, config.mMorning))
    }

    private fun perRequest(h: Int, m: Int): PeriodicWorkRequest {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, h)
            set(Calendar.MINUTE, m)
        }
        if (target.before(now)) target.add(Calendar.DAY_OF_YEAR, 1)
        val delta = target.timeInMillis - now.timeInMillis

        val notificationData = Data.Builder()
            .putString("NOTIFICATION_TYPE", "DAILY_REMIND")
            .build()

        val request = PeriodicWorkRequestBuilder<WorkerReminder>(1, TimeUnit.DAYS)
            .setInitialDelay(delta, TimeUnit.MILLISECONDS)
            .setInputData(notificationData)
            .build()
        return request
    }
}