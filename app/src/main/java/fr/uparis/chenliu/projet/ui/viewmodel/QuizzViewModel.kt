package fr.uparis.chenliu.projet.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.uparis.chenliu.projet.R
import fr.uparis.chenliu.projet.data.database.AppDatabase
import fr.uparis.chenliu.projet.data.entity.SettingsEntity
import fr.uparis.chenliu.projet.data.entity.UsersEntity
import kotlinx.coroutines.launch

class QuizzViewModel(application: Application): AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private var user = mutableStateOf<UsersEntity?>(null)
    private var settings = mutableStateOf<SettingsEntity?>(null)

    val energy = mutableIntStateOf(0)
    val subscription = mutableStateOf(false)

    val font = mutableIntStateOf(0)

    var question = mutableStateOf("")
    val answers: Array<MutableState<AnswerSummary>> = Array(4) { mutableStateOf(AnswerSummary(-1,"",false)) }

    data class AnswerSummary(
        var answerId: Int,
        var answer: String,
        var validity: Boolean
    )

    fun init() {
        viewModelScope.launch {
            user.value = db.usersDao().getUser()
            energy.intValue = user.value!!.energy
            subscription.value = user.value!!.subscribe

            settings.value = db.settingsDao().getSetting()
            font.intValue = settings.value!!.font
        }
    }

    fun getUsers() {
        viewModelScope.launch {
            user.value = db.usersDao().getUser()
            energy.intValue = user.value!!.energy
            subscription.value = user.value!!.subscribe
        }
    }

    fun getQuestionAndAnswer(subject: String, difficulty: String) {
        viewModelScope.launch {
            when (subject) {
                "Maths" -> {
                    val currentDao = db.quizzMathsDao()
                    val currentQuestion = currentDao.getQuestion(difficulty)
                    // question
                    question.value = currentQuestion.question
                    // responses
                    val bad = currentDao.getWrongAnswers(currentQuestion.idQuestion)
                    val good = currentDao.getCorrectAnswer(currentQuestion.idQuestion)
                    val res = (bad + good).shuffled()
                    for (i in 0..res.size - 1) {
                        answers[i].value = res[i]
                    }
                }
                "Histoire" -> {
                    val currentDao = db.quizzHistoryDao()
                    val currentQuestion = currentDao.getQuestion(difficulty)
                    // question
                    question.value = currentQuestion.question
                    // responses
                    val bad = currentDao.getWrongAnswers(currentQuestion.idQuestion)
                    val good = currentDao.getCorrectAnswer(currentQuestion.idQuestion)
                    val res = (bad + good).shuffled()
                    for (i in 0..res.size - 1) {
                        answers[i].value = res[i]
                    }
                }
                "Géographie" -> {
                    val currentDao = db.quizzGeoDao()
                    val currentQuestion = currentDao.getQuestion(difficulty)
                    // question
                    question.value = currentQuestion.question
                    // responses
                    val bad = currentDao.getWrongAnswers(currentQuestion.idQuestion)
                    val good = currentDao.getCorrectAnswer(currentQuestion.idQuestion)
                    val res = (bad + good).shuffled()
                    for (i in 0..res.size - 1) {
                        answers[i].value = res[i]
                    }
                }
                "Informatique" -> {
                    val currentDao = db.quizzInfoDao()
                    val currentQuestion = currentDao.getQuestion(difficulty)
                    // question
                    question.value = currentQuestion.question
                    // responses
                    val bad = currentDao.getWrongAnswers(currentQuestion.idQuestion)
                    val good = currentDao.getCorrectAnswer(currentQuestion.idQuestion)
                    val res = (bad + good).shuffled()
                    for (i in 0..res.size - 1) {
                        answers[i].value = res[i]
                    }
                }
                else -> db.quizzMathsDao()
            }
        }
    }

    fun removeEnergy() {
        if (!subscription.value) {
            val maxEnergy = 20
            viewModelScope.launch {
                if (energy.intValue <= 0) return@launch
                energy.intValue -= 1
                if (energy.intValue == maxEnergy - 1) {
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

    fun translateFont(): List<Int> {
        val ft = when(font.intValue) {
            0 -> 0
            1 -> R.font.game
            2 -> R.font.typewritter
            3 -> R.font.pixel
            4 -> R.font.pokemon
            else -> 0
        }

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
}