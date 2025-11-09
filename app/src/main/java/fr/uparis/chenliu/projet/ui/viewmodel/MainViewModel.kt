package fr.uparis.chenliu.projet.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import fr.uparis.chenliu.projet.R
import fr.uparis.chenliu.projet.data.database.AppDatabase
import fr.uparis.chenliu.projet.data.entity.AnswerGeoEntity
import fr.uparis.chenliu.projet.data.entity.AnswerHistoryEntity
import fr.uparis.chenliu.projet.data.entity.AnswerInfoEntity
import fr.uparis.chenliu.projet.data.entity.AnswerMathsEntity
import fr.uparis.chenliu.projet.data.entity.QuizzGeoEntity
import fr.uparis.chenliu.projet.data.entity.QuizzHistoryEntity
import fr.uparis.chenliu.projet.data.entity.QuizzInfoEntity
import fr.uparis.chenliu.projet.data.entity.QuizzMathsEntity
import fr.uparis.chenliu.projet.data.entity.SettingsEntity
import fr.uparis.chenliu.projet.data.entity.UsersEntity
import fr.uparis.chenliu.projet.data.model.AnswerData
import fr.uparis.chenliu.projet.data.model.AnswerParameter
import fr.uparis.chenliu.projet.data.model.QuestionData
import fr.uparis.chenliu.projet.data.model.QuestionParameter
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
        quizz(c, R.raw.quizzmaths)
        quizz(c, R.raw.quizzinfo)
        quizz(c, R.raw.quizzhistory)
        quizz(c, R.raw.quizzgeo)
    }

    suspend fun quizz(c: Context, quizz: Int) {
        val inputStream = c.resources.openRawResource(quizz)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val questionData = Gson().fromJson(jsonString, QuestionParameter::class.java)
        val qData = questionData.question

        val answerData = Gson().fromJson(jsonString, AnswerParameter::class.java)
        val aData = answerData.answer

        quizzQuestion(qData, quizz)
        quizzAnwser(aData, quizz)
    }

    suspend fun quizzQuestion(qData: List<QuestionData>, quizz: Int) {
        qData.forEach {
            when (quizz) {
                R.raw.quizzmaths -> {
                    db.quizzMathsDao().insertQuestion(
                        QuizzMathsEntity(
                            difficulty = it.difficulty,
                            question = it.question,
                            resources = it.resources
                        )
                    )
                }
                R.raw.quizzinfo -> {
                    db.quizzInfoDao().insertQuestion(
                        QuizzInfoEntity(
                            difficulty = it.difficulty,
                            question = it.question,
                            resources = it.resources
                        )
                    )
                }
                R.raw.quizzgeo -> {
                    db.quizzGeoDao().insertQuestion(
                        QuizzGeoEntity(
                            difficulty = it.difficulty,
                            question = it.question,
                            resources = it.resources
                        )
                    )
                }
                R.raw.quizzhistory -> {
                    db.quizzHistoryDao().insertQuestion(
                        QuizzHistoryEntity(
                            difficulty = it.difficulty,
                            question = it.question,
                            resources = it.resources
                        )
                    )
                }
            }
        }
    }

    suspend fun quizzAnwser(aData: List<AnswerData>, quizz: Int) {
        aData.forEach {
            when (quizz) {
                R.raw.quizzmaths -> {
                    db.quizzMathsDao().insertAnswer(
                        AnswerMathsEntity(
                            idQuestion = it.idQuestion,
                            answer = it.answer,
                            validity = it.validity,
                            position = it.position
                        )
                    )
                }
                R.raw.quizzinfo -> {
                    db.quizzInfoDao().insertAnswer(
                        AnswerInfoEntity(
                            idQuestion = it.idQuestion,
                            answer = it.answer,
                            validity = it.validity,
                            position = it.position
                        )
                    )
                }
                R.raw.quizzgeo -> {
                    db.quizzGeoDao().insertAnswer(
                        AnswerGeoEntity(
                            idQuestion = it.idQuestion,
                            answer = it.answer,
                            validity = it.validity,
                            position = it.position
                        )
                    )
                }
                R.raw.quizzhistory -> {
                    db.quizzHistoryDao().insertAnswer(
                        AnswerHistoryEntity(
                            idQuestion = it.idQuestion,
                            answer = it.answer,
                            validity = it.validity,
                            position = it.position
                        )
                    )
                }
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