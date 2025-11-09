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
import fr.uparis.chenliu.projet.data.model.AnswerParameter
import fr.uparis.chenliu.projet.data.model.QuestionParameter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private var user = mutableStateOf<UsersEntity?>(null)

    var username = mutableStateOf("")
    val duration = mutableStateOf("")
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
                duration.value = user.value!!.duration
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
        db.usersDao().insertUser(UsersEntity(username = username.value.trim(), subscribe = false, duration = "", games = 0, wins = 0))
        user.value = db.usersDao().getUser()
        db.settingsDao().init(SettingsEntity(user.value!!.userId, 1f, 1f, 0, 0, ""))
    }

    suspend fun initQuizz(c:Context) {

            var inputStream = c.resources.openRawResource(R.raw.quizzmaths)
            var jsonString = inputStream.bufferedReader().use { it.readText() }
            var questionData = Gson().fromJson(jsonString, QuestionParameter::class.java)
            var qData = questionData.question
            qData.forEach {
                db.quizzMathsDao().insertQuestion(
                    QuizzMathsEntity(
                        difficulty = it.difficulty,
                        question = it.question,
                        resources = it.resources
                    )
                )
            }
            var answerData = Gson().fromJson(jsonString, AnswerParameter::class.java)
            var aData = answerData.answer
            aData.forEach {
                db.quizzMathsDao().insertAnswer(
                    AnswerMathsEntity(
                        idQuestion = it.idQuestion,
                        answer = it.answer,
                        validity = it.validity,
                        position = it.position
                    )
                )
            }
            inputStream = c.resources.openRawResource(R.raw.quizzinfo)
            jsonString = inputStream.bufferedReader().use { it.readText() }
            questionData = Gson().fromJson(jsonString, QuestionParameter::class.java)
            qData = questionData.question
            qData.forEach {
                db.quizzInfoDao().insertQuestion(
                    QuizzInfoEntity(
                        difficulty = it.difficulty,
                        question = it.question,
                        resources = it.resources
                    )
                )
            }
            answerData = Gson().fromJson(jsonString, AnswerParameter::class.java)
            aData = answerData.answer
            aData.forEach {
                db.quizzInfoDao().insertAnswer(
                    AnswerInfoEntity(
                        idQuestion = it.idQuestion,
                        answer = it.answer,
                        validity = it.validity,
                        position = it.position
                    )
                )
            }
            inputStream = c.resources.openRawResource(R.raw.quizzhistory)
            jsonString = inputStream.bufferedReader().use { it.readText() }
            questionData = Gson().fromJson(jsonString, QuestionParameter::class.java)
            qData = questionData.question
            qData.forEach {
                db.quizzHistoryDao().insertQuestion(
                    QuizzHistoryEntity(
                        difficulty = it.difficulty,
                        question = it.question,
                        resources = it.resources
                    )
                )
            }
            answerData = Gson().fromJson(jsonString, AnswerParameter::class.java)
            aData = answerData.answer
            aData.forEach {
                db.quizzHistoryDao().insertAnswer(
                    AnswerHistoryEntity(
                        idQuestion = it.idQuestion,
                        answer = it.answer,
                        validity = it.validity,
                        position = it.position
                    )
                )
            }
            inputStream = c.resources.openRawResource(R.raw.quizzgeo)
            jsonString = inputStream.bufferedReader().use { it.readText() }
            questionData = Gson().fromJson(jsonString, QuestionParameter::class.java)
            qData = questionData.question
            qData.forEach {
                db.quizzGeoDao().insertQuestion(
                    QuizzGeoEntity(
                        difficulty = it.difficulty,
                        question = it.question,
                        resources = it.resources
                    )
                )
            }
            answerData = Gson().fromJson(jsonString, AnswerParameter::class.java)
            aData = answerData.answer
            aData.forEach {
                db.quizzGeoDao().insertAnswer(
                    AnswerGeoEntity(
                        idQuestion = it.idQuestion,
                        answer = it.answer,
                        validity = it.validity,
                        position = it.position
                    )
                )
            }

    }

    fun expireSubscription() {
        viewModelScope.launch {
            db.usersDao().updateSubscribe(userId = user.value!!.userId, subscribe = false)
            db.usersDao().updateDuration(userId = user.value!!.userId, duration = "")
        }
    }
}