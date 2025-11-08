package fr.uparis.chenliu.projet.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import fr.uparis.chenliu.projet.R
import fr.uparis.chenliu.projet.data.dao.QuizzDao
import fr.uparis.chenliu.projet.data.model.AnswerParameter
import fr.uparis.chenliu.projet.data.model.QuestionParameter
import fr.uparis.chenliu.projet.data.database.AppDatabase
import fr.uparis.chenliu.projet.data.entity.AnswerEntity
import fr.uparis.chenliu.projet.data.entity.QuestionEntity
import fr.uparis.chenliu.projet.data.entity.SettingsEntity
import fr.uparis.chenliu.projet.data.entity.UsersEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.jvm.java

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

                db.usersDao().insertUser(UsersEntity(username = username.value.trim(), subscribe = false, games = 0, wins = 0))
                user.value = db.usersDao().getUser()
                db.settingsDao().init(SettingsEntity(user.value!!.userId, 1f, 1f, 0, 0, ""))
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