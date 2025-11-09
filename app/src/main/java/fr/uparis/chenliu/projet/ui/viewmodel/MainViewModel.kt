package fr.uparis.chenliu.projet.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.uparis.chenliu.projet.data.database.AppDatabase
import fr.uparis.chenliu.projet.data.entity.SettingsEntity
import fr.uparis.chenliu.projet.data.entity.UsersEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private var user = mutableStateOf<UsersEntity?>(null)

    var username = mutableStateOf("")
    val endDate = mutableStateOf("")
    var loading = mutableStateOf(true)
    var exist = mutableStateOf(false)

    init {
        viewModelScope.launch {
            user.value = db.usersDao().getUser()
            if (user.value == null) {
                exist.value = false
            } else {
                exist.value = true
                username.value = user.value!!.username
                endDate.value = user.value!!.endDate
            }
            delay(3000)
            loading.value = false
        }
    }

    fun insertUsers() {
        loading.value = true
        viewModelScope.launch {
            if (!db.usersDao().isExist()) {
                if (username.value == "") {
                    username.value = "Joueur"
                }

                db.usersDao().insertUser(UsersEntity(username = username.value.trim(), energy = 20, lastTimeEnergy = System.currentTimeMillis(),subscribe = false, endDate = "", games = 0, wins = 0))
                user.value = db.usersDao().getUser()
                db.settingsDao().init(SettingsEntity(userId = user.value!!.userId, music = 1f, sound = 1f, background = 0, font = 0, preferredTopics = ""))
                exist.value = true
                loading.value = false
            }
        }
    }

    fun expireSubscription() {
        viewModelScope.launch {
            db.usersDao().updateSubscribe(userId = user.value!!.userId, subscribe = false)
            db.usersDao().updateDuration(userId = user.value!!.userId, endDate = "")
        }
    }
}