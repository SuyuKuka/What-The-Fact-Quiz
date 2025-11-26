package fr.uparis.chenliu.projet.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.uparis.chenliu.projet.R
import fr.uparis.chenliu.projet.data.database.AppDatabase
import fr.uparis.chenliu.projet.data.entity.LeaderboardEntity
import fr.uparis.chenliu.projet.data.entity.SettingsEntity
import fr.uparis.chenliu.projet.data.entity.UsersEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MenuViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val user = mutableStateOf<UsersEntity?>(null)
    private val settings = mutableStateOf<SettingsEntity?>(null)
    private val leaderboard = mutableStateOf<List<LeaderboardEntity?>>(emptyList())

    // Users
    val username = mutableStateOf("")
    val maxEnergy = mutableIntStateOf(20)
    val energy = mutableIntStateOf(0)
    val lastTimeEnergy = mutableLongStateOf(System.currentTimeMillis())
    val timeEnergy = mutableStateOf("")
    val subscription = mutableStateOf(false)
    val endDate = mutableStateOf("")

    // Settings
    val backgroundList = MutableStateFlow<List<String>>(emptyList())
    val fontList = MutableStateFlow<List<String>>(emptyList())
    val decoNameList = MutableStateFlow<List<String>>(emptyList())
    val decoPathList = MutableStateFlow<List<String>>(emptyList())
    val decoTypeList = MutableStateFlow<List<Int>>(emptyList())
    val decoDiffList = MutableStateFlow<List<Int>>(emptyList())
    val background = mutableIntStateOf(0)
    val currentBackground = mutableIntStateOf(R.drawable.background_default_blurred)
    val font = mutableIntStateOf(0)
    val currentFont = mutableStateOf(listOf(0, 0))
    val preferredTopics = mutableStateOf("")

    // Leaderboard
    val usernameLeaderboard = mutableListOf<String?>()
    val score = mutableListOf<Int?>()
    val bestTime = mutableListOf<Double?>()
    val subject = mutableStateOf("")

    // Others
    val difficultyList = listOf("Facile", "Moyen", "Difficile")
    val saved = mutableStateOf(true)
    val changed = mutableStateOf(false)
    val subjectList = MutableStateFlow<List<String>>(emptyList())
    val subjectImageList = MutableStateFlow<List<String>>(emptyList())

    fun init(username: String, context: Context) {
        this.username.value = username
        viewModelScope.launch {
            // init des themes
            subjectList.value = db.subjectDao().getAllSubjectNames()
            subjectImageList.value = db.subjectDao().getAllImageNames()
            // init des paramettres utilisateur
            getUsers()
            settings.value = db.settingsDao().getSetting()
            background.intValue = settings.value!!.background
            font.intValue = settings.value!!.font
            preferredTopics.value = settings.value!!.preferredTopics
            // init du leaderboard
            leaderboard.value = db.leaderboardDao().getLeaderboard(subjectList.value[0])
            changeSubject(subjectList.value[0])
            subject.value = subjectList.value[0]
            // init des deco
            decoNameList.value = db.decoDao().getAllDecoNames()
            decoPathList.value = db.decoDao().getAllDecoPaths()
            decoTypeList.value = db.decoDao().getAllDecoTypes()
            decoDiffList.value = db.decoDao().getAllDecoDiff()
            for (i in 0..decoTypeList.value.size - 1) {
                if (decoTypeList.value[i] == 0) {
                    backgroundList.value += decoNameList.value[i]
                } else {
                    fontList.value += decoNameList.value[i]
                }
            }

            updateCurrentBackground(context)
            updateCurrentFont(context)

            while (true) {
                getUsers()
                addEnergy()
                delay(1000)
            }
        }
    }

    fun manageSubscription(isMonthly: Boolean, isBuying: Boolean) {
        viewModelScope.launch {
            val day = mutableIntStateOf(0)
            val month = mutableIntStateOf(0)
            val year = mutableIntStateOf(0)

            db.usersDao().updateEnergy(userId = user.value!!.userId, energy = 20, lastTimeEnergy = System.currentTimeMillis())
            getUsers()
            if (isBuying) {
                val today = mutableStateOf<LocalDateTime>(LocalDateTime.now())
                day.intValue = today.value.dayOfMonth.toString().toInt()
                month.intValue = today.value.month.value.toString().toInt()
                year.intValue = today.value.year.toString().toInt()
            } else {
                val endDate = endDate.value.split("/")
                day.intValue = endDate[0].toInt()
                month.intValue = endDate[1].toInt()
                year.intValue = endDate[2].toInt()
            }

            if (isMonthly) {
                if (month.intValue == 12) {
                    year.intValue += 1
                    month.intValue = 1
                } else {
                    month.intValue += 1
                }
            } else {
                year.intValue += 1
            }
            db.usersDao().updateSubscribe(userId = user.value!!.userId, subscribe = true)
            db.usersDao().updateDuration(userId = user.value!!.userId, endDate = "${day.intValue}/${month.intValue}/${year.intValue}")
            getUsers()
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
            if (username.value.trim() == "") {
                username.value = "Joueur"
            }
            db.usersDao().updateUsername(userId = user.value!!.userId, username = username.value.trim())
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
                background = background.intValue,
                font = font.intValue,
                preferredTopics = preferredTopics.value
            )
            db.settingsDao().updateSettings(newSettings)
            settings.value = newSettings
        }
    }

    fun undoSettings(context: Context) {
        background.intValue = settings.value!!.background
        font.intValue = settings.value!!.font
        preferredTopics.value = settings.value!!.preferredTopics
        updateCurrentBackground(context)
        updateCurrentFont(context)
    }

    fun updateCurrentBackground(context: Context) {
        if (decoPathList.value.isEmpty()) {
            currentBackground.intValue = R.drawable.background_default_blurred
            return
        }
        val resources = context.resources
        currentBackground.intValue = resources.getIdentifier(
            decoPathList.value[background.intValue],
            "drawable",
            context.packageName
        )
    }

    fun updateCurrentFont(context: Context) {
        if (decoPathList.value.isEmpty()) {
            currentFont.value = listOf(0, 0)
            return
        }
        val resources = context.resources
        val fontId = resources.getIdentifier(
            decoPathList.value[font.intValue + backgroundList.value.size],
            "font",
            context.packageName
        )
        val fontDiff = decoDiffList.value[font.intValue + backgroundList.value.size]
        currentFont.value = listOf(fontId, fontDiff)
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
            if (energy.intValue <= 0) return
            viewModelScope.launch {
                val lastTime = mutableLongStateOf(0)
                energy.intValue -= 1
                if (energy.intValue == maxEnergy.intValue - 1) {
                     lastTime.longValue = System.currentTimeMillis()
                } else {
                    lastTime.longValue = user.value!!.lastTimeEnergy
                }

                db.usersDao().updateEnergy(
                    userId = user.value!!.userId,
                    energy = energy.intValue,
                    lastTimeEnergy = lastTime.longValue
                )
                getUsers()
            }
        }
    }

    // pour leaderboard
    fun changeSubject(subject: String) {
        usernameLeaderboard.clear()
        score.clear()
        bestTime.clear()
        this.subject.value = subject
        viewModelScope.launch {
            leaderboard.value = db.leaderboardDao().getLeaderboard(subject)
            if (leaderboard.value.isNotEmpty()) {
                for (i in 0..leaderboard.value.size - 1) {
                    if (leaderboard.value[i]?.userId != null) {
                        usernameLeaderboard.add(db.usersDao().getUsername(leaderboard.value[i]!!.userId))
                        score.add(leaderboard.value[i]!!.score)
                        bestTime.add(leaderboard.value[i]!!.time)
                    } else {
                        usernameLeaderboard[i] = null
                    }
                }
            }
            changed.value = false
        }
    }
}