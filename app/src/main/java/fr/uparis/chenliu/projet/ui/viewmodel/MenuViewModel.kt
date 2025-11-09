package fr.uparis.chenliu.projet.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.uparis.chenliu.projet.R
import fr.uparis.chenliu.projet.data.database.AppDatabase
import fr.uparis.chenliu.projet.data.entity.SettingsEntity
import fr.uparis.chenliu.projet.data.entity.UsersEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MenuViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private var user = mutableStateOf<UsersEntity?>(null)
    private var settings = mutableStateOf<SettingsEntity?>(null)

    val username = mutableStateOf("")
    val energy = mutableIntStateOf(0)
    val lastTimeEnergy = mutableLongStateOf(System.currentTimeMillis())
    val timeEnergy = mutableStateOf("")
    val subscription = mutableStateOf(false)
    val endDate = mutableStateOf("")

    val music = mutableFloatStateOf(0f)
    val sound = mutableFloatStateOf(0f)
    val background = mutableIntStateOf(0)
    val font = mutableIntStateOf(0)
    val preferredTopics = mutableStateOf("")

    val backgroundList = listOf("Fond 1", "Fond 2", "Fond 3")
    val fontList = listOf("Default", "Game", "Typewriter", "Pixel", "Pokemon")
    val saved = mutableStateOf(true)

    val maxEnergy = mutableIntStateOf(20)

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
            while (true) {
                getUsers()
                addEnergy()
                println(background.intValue.toString() + "-------" + font.intValue.toString())
                delay(1000)
            }
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
            db.usersDao().updateDuration(userId = user.value!!.userId, endDate = "${day}/${month}/${year}")
            getUsers()
        }
    }

    fun extendSubscription(isMonthly: Boolean) {
        viewModelScope.launch {
            getUsers()

            val endDate = endDate.value.split("/")
            val day = endDate[0]
            var month = endDate[1].toInt()
            var year = endDate[2].toInt()
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
            db.usersDao().updateDuration(userId = user.value!!.userId, endDate = "${day}/${month}/${year}")

        }
    }

    fun getUsers() {
        viewModelScope.launch {
            user.value = db.usersDao().getUser()
            energy.intValue = user.value!!.energy
            lastTimeEnergy.longValue = user.value!!.lastTimeEnergy
            subscription.value = user.value!!.subscribe
            endDate.value = user.value!!.endDate
        }
    }

    fun updateUsername() {
        viewModelScope.launch {
            db.usersDao().updateUsername(userId = user.value!!.userId, username = username.value)
            getUsers()
        }
    }

    fun undoUsername() {
        username.value = user.value!!.username
    }

    fun updateSettings() {
        viewModelScope.launch {
            val newSettings = SettingsEntity(
                userId = user.value!!.userId,
                music = sound.floatValue,
                sound = music.floatValue,
                background = background.intValue,
                font = font.intValue,
                preferredTopics = preferredTopics.value
            )
            db.settingsDao().updateSettings(newSettings)
            settings.value = newSettings
        }
    }

    fun undoSettings() {
        music.floatValue = settings.value!!.music
        sound.floatValue = settings.value!!.sound
        background.intValue = settings.value!!.background
        font.intValue = settings.value!!.font
        preferredTopics.value = settings.value!!.preferredTopics
    }

    fun translateBackground(): Int{
        val bg = when(background.intValue) {
            0 -> R.drawable.b0
            1 -> R.drawable.b1
            2 -> R.drawable.b2
            else -> R.drawable.b0
        }
        return bg
    }

    fun translateFont(): List<Int> {
        val ft = when(font.intValue) {
            0 -> 0
            1 -> R.font.game
            2 -> R.font.typewritter
            3 -> R.font.pixel
            4 -> R.font.pokemon
            else -> 0
        }

        // différence de taille entre default et autres (pour avoir meme taille quasiment)
        val sz = when(font.intValue) {
            0 -> 0
            1 -> -1
            2 -> 1
            3 -> -5
            4 -> -2
            else -> 0
        }
        return listOf(ft, sz)
    }

    fun addEnergy() {
        viewModelScope.launch {
            if (user.value == null) return@launch
            if (energy.intValue >= maxEnergy.intValue) return@launch

            val cycleEnergy = 2
            val now = System.currentTimeMillis()
            val refill = ((now - lastTimeEnergy.longValue) / 60000L) / cycleEnergy
            val time = (((((now - lastTimeEnergy.longValue) / (cycleEnergy * 60000L) * cycleEnergy * 60000L) + lastTimeEnergy.longValue) + cycleEnergy * 60000L) - now) / 1000L
            timeEnergy.value = "${time / 60L}min ${time % 60L}s"

            if (refill <= 0) return@launch

            energy.intValue += refill.toInt()
            if (energy.intValue > maxEnergy.intValue) energy.intValue = maxEnergy.intValue

            lastTimeEnergy.longValue += refill.toInt() * cycleEnergy * 60000L

            db.usersDao().updateEnergy(userId = user.value!!.userId, energy = energy.intValue, lastTimeEnergy = lastTimeEnergy.longValue)
            getUsers()
        }
    }

    fun removeEnergy() {
        if (!subscription.value) {
            viewModelScope.launch {
                if (energy.intValue <= 0) return@launch
                energy.intValue -= 1
                if (energy.intValue == maxEnergy.intValue - 1) {
                    db.usersDao().updateEnergy(
                        userId = user.value!!.userId,
                        energy = energy.intValue,
                        lastTimeEnergy = System.currentTimeMillis()
                    )
                } else {
                    db.usersDao().updateEnergy(
                        userId = user.value!!.userId,
                        energy = energy.intValue,
                        lastTimeEnergy = user.value!!.lastTimeEnergy
                    )
                }
                getUsers()
            }
        }
    }
}