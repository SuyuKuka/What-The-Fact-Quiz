package fr.uparis.chenliu.projet.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import fr.uparis.chenliu.projet.data.store.TimeConfig
import fr.uparis.chenliu.projet.ui.worker.WorkerReminder

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "time")

class TimeConfigViewModel(val app: Application) : AndroidViewModel(app) {
    private val keyHM = intPreferencesKey("hour_morning")
    private val keyMM = intPreferencesKey("minute_morning")

    private val store = app.dataStore

    fun save(config: TimeConfig) {
        viewModelScope.launch {
            store.edit {
                it[keyHM] = config.hMorning
                it[keyMM] = config.mMorning
            }
        }
    }

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