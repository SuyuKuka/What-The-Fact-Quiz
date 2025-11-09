package fr.uparis.chenliu.projet.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import fr.uparis.chenliu.projet.R
import fr.uparis.chenliu.projet.data.database.AppDatabase
import fr.uparis.chenliu.projet.data.entity.QuizzScienceEntity
import fr.uparis.chenliu.projet.data.entity.SettingsEntity
import fr.uparis.chenliu.projet.data.entity.UsersEntity
import fr.uparis.chenliu.projet.data.model.JsonReader

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.jvm.java

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
                delay(1000)
            } else {
                exist.value = true
                username.value = user.value!!.username
                endDate.value = user.value!!.endDate
                delay(3000)
            }
            loading.value = false
        }
    }

    fun insertData(context: Context) {
        loading.value = true
        viewModelScope.launch {
            if (!db.usersDao().isExist()) {
                if (username.value == "") {
                    username.value = "Joueur"
                }
                initUser()
                initQuizz(context)

                exist.value = true
                loading.value = false
            }
        }
    }

    suspend fun initUser() {
        db.usersDao().insertUser(UsersEntity(username = username.value.trim(), energy = 20, lastTimeEnergy = System.currentTimeMillis(),subscribe = false, endDate = "", games = 0, wins = 0))
        user.value = db.usersDao().getUser()
        db.settingsDao().init(SettingsEntity(userId = user.value!!.userId, music = 1f, sound = 1f, background = 0, font = 0, preferredTopics = ""))
        delay(3000)
    }

    suspend fun initQuizz(c:Context) {
        quizz(c, R.raw.science)
//        quizz(c, R.raw.quizzinfo)
//        quizz(c, R.raw.quizzhistory)
//        quizz(c, R.raw.quizzgeo)
    }

    suspend fun quizz(c: Context, quizz: Int) {
        val inputStream = c.resources.openRawResource(quizz)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val quizzData = Gson().fromJson(jsonString, JsonReader.QuizzParameter::class.java)

        quizzQuestion(quizzData.quizz, quizz)
    }

    suspend fun quizzQuestion(qData: List<JsonReader.QuizzData>, quizz: Int) {
        qData.forEach {
            when (quizz) {
                R.raw.science -> {
                    db.quizzMathsDao().insertQuestion(
                        QuizzScienceEntity(
                            id = it.id,
                            question = it.question,
                            propositions = it.propositions,
                            answer = it.answer,
                            difficulty = it.difficulty
                        )
                    )
                }
//                R.raw.quizzinfo -> {
//                    db.quizzInfoDao().insertQuestion(
//                        QuizzInfoEntity(
//                            difficulty = it.difficulty,
//                            question = it.question,
//                            resources = it.resources
//                        )
//                    )
//                }
//                R.raw.quizzgeo -> {
//                    db.quizzGeoDao().insertQuestion(
//                        QuizzGeoEntity(
//                            difficulty = it.difficulty,
//                            question = it.question,
//                            resources = it.resources
//                        )
//                    )
//                }
//                R.raw.quizzhistory -> {
//                    db.quizzHistoryDao().insertQuestion(
//                        QuizzHistoryEntity(
//                            difficulty = it.difficulty,
//                            question = it.question,
//                            resources = it.resources
//                        )
//                    )
//                }
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