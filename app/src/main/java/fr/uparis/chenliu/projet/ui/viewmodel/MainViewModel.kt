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

                db.usersDao().insertUser(UsersEntity(username = username.value, subscribe = false, games = 0, wins = 0))
                user.value = db.usersDao().getUser()
                db.settingsDao().init(SettingsEntity(user.value!!.userId, 100, 100, 0, "sans-serif", ""))
                exist.value = true
                loading.value = false
            }
        }
    }

    fun deleteAllUsers() {
        viewModelScope.launch {
            db.usersDao().deleteAllUsers()
        }
    }
}