package fr.uparis.chenliu.projet.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import fr.uparis.chenliu.projet.R
import fr.uparis.chenliu.projet.data.database.AppDatabase
import fr.uparis.chenliu.projet.data.entity.DecoEntity
import fr.uparis.chenliu.projet.data.entity.QuizzEntity
import fr.uparis.chenliu.projet.data.entity.SettingsEntity
import fr.uparis.chenliu.projet.data.entity.SubjectEntity
import fr.uparis.chenliu.projet.data.entity.UsersEntity
import fr.uparis.chenliu.projet.data.model.JsonReader

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.jvm.java

class MainViewModel(application: Application): AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val user = mutableStateOf<UsersEntity?>(null)

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
                if (username.value.trim() == "") {
                    username.value = "Joueur"
                }
                initSubject(context)
                initQuizz(context)
                initUser()
                initDeco(context)

                exist.value = true
                loading.value = false
            }
        }
    }

    suspend fun initSubject(c: Context) {
        val inputStream = c.resources.openRawResource(R.raw.subject)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val data = Gson().fromJson(jsonString, JsonReader.SubjectParameter::class.java)

        data.subject.forEach {
            db.subjectDao().insertTheme(
                SubjectEntity(
                    name = it.name,
                    imageName = it.imageName
                )
            )
        }
    }

    suspend fun initQuizz(c:Context) {
        val inputStream = c.resources.openRawResource(R.raw.quizz)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val data = Gson().fromJson(jsonString, JsonReader.QuizzParameter::class.java)

        data.quizz.forEach {
            db.quizzDao().insertQuestion(
                QuizzEntity(
                    type = it.type,
                    question = it.question,
                    propositions = it.propositions,
                    answer = it.answer,
                    difficulty = it.difficulty,
                    subject = it.subject
                )
            )
        }
    }

    suspend fun initDeco(c: Context) {
        val inputStream = c.resources.openRawResource(R.raw.deco)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val data = Gson().fromJson(jsonString, JsonReader.DecoParameter::class.java)

        data.deco.forEach {
            db.decoDao().insertDeco(
                DecoEntity(
                    name = it.name,
                    path = it.path,
                    type = it.type,
                    diff = it.diff
                )
            )
        }
    }

    suspend fun initUser() {
        db.usersDao().insertUser(UsersEntity(username = username.value.trim(), energy = 20, lastTimeEnergy = System.currentTimeMillis(),subscribe = false, endDate = ""))
        user.value = db.usersDao().getUser()
        db.settingsDao().init(SettingsEntity(userId = user.value!!.userId, background = 0, font = 0, preferredTopics = ""))
        delay(3000)
    }


    fun expireSubscription() {
        viewModelScope.launch {
            db.usersDao().updateSubscribe(userId = user.value!!.userId, subscribe = false)
            db.usersDao().updateDuration(userId = user.value!!.userId, endDate = "")
        }
    }
}