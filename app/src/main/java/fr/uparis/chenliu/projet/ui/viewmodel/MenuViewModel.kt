package fr.uparis.chenliu.projet.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.uparis.chenliu.projet.R
import fr.uparis.chenliu.projet.data.database.AppDatabase
import fr.uparis.chenliu.projet.data.entity.SettingsEntity
import fr.uparis.chenliu.projet.data.entity.UsersEntity
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MenuViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private var user = mutableStateOf<UsersEntity?>(null)
    private var settings = mutableStateOf<SettingsEntity?>(null)

    val username = mutableStateOf("")
    val subscription = mutableStateOf(false)
    val duration = mutableStateOf("")

    val music = mutableFloatStateOf(0f)
    val sound = mutableFloatStateOf(0f)
    val background = mutableIntStateOf(0)
    val font = mutableIntStateOf(0)
    val preferredTopics = mutableStateOf("")

    val backgroundList = listOf("Fond 1", "Fond 2", "Fond 3")
    val fontList = listOf("Police 1", "Police 2", "Police 3")
    val saved = mutableStateOf(true)

    fun init(username: String) {
        this.username.value = username
        viewModelScope.launch {
            getUsers()
            settings.value = db.settingsDao().getSetting()

            music.floatValue = settings.value!!.music
            sound.floatValue = settings.value!!.sound
            background.intValue = settings.value!!.background
            font.intValue = settings.value!!.font
            preferredTopics.value = settings.value!!.preferredTopics
        }
    }

    fun buySubscription(isMonthly: Boolean) {
        viewModelScope.launch {
            val today = mutableStateOf<LocalDateTime>(LocalDateTime.now())
            val day = today.value.dayOfMonth.toString().toInt()
            var month = today.value.month.value.toString().toInt()
            var year = today.value.year.toString().toInt()
            if (isMonthly) {
                if (month == 12) {
                    year += 1
                    month = 1
                } else {
                    month += 1
                }
            } else {
                year += 1
            }
            db.usersDao().updateSubscribe(userId = user.value!!.userId, subscribe = true)
            db.usersDao().updateDuration(userId = user.value!!.userId, duration = "${day}/${month}/${year}")
            getUsers()
        }
    }

    fun extendSubscription(isMonthly: Boolean) {
        viewModelScope.launch {
            getUsers()

            val duration = duration.value.split("/")
            val day = duration[0]
            var month = duration[1].toInt()
            var year = duration[2].toInt()
            if (isMonthly) {
                if (month == 12) {
                    year += 1
                    month = 1
                } else {
                    month += 1
                }
            } else {
                year += 1
            }
            db.usersDao().updateSubscribe(userId = user.value!!.userId, subscribe = true)
            db.usersDao().updateDuration(userId = user.value!!.userId, duration = "${day}/${month}/${year}")
        }
    }

    fun getUsers() {
        viewModelScope.launch {
            user.value = db.usersDao().getUser()
            duration.value = user.value!!.duration
            subscription.value = user.value!!.subscribe
        }
    }

    fun updateSettings() {
        viewModelScope.launch {
            db.settingsDao().updateSettings(
                SettingsEntity(
                    userId = user.value!!.userId,
                    music = sound.floatValue,
                    sound = music.floatValue,
                    background = background.intValue,
                    font = font.intValue,
                    preferredTopics = preferredTopics.value
                )
            )
        }
    }

    fun translateBackground(): Int {
        val background = when(background.intValue) {
            0 -> R.drawable.b0
            1 -> R.drawable.b1
            2 -> R.drawable.b2
            else -> {
                R.drawable.b0
            }
        }
        return background
    }

    fun undoSettings() {
        music.floatValue = settings.value!!.music
        sound.floatValue = settings.value!!.sound
        background.intValue = settings.value!!.background
        font.intValue = settings.value!!.font
        preferredTopics.value = settings.value!!.preferredTopics
    }
}