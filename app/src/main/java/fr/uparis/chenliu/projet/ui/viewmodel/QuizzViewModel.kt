package fr.uparis.chenliu.projet.ui.viewmodel

import android.app.Application
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

    var question = mutableListOf<String>()
    val answers = mutableListOf<AnswerSummary>()
    val loading = mutableStateOf(true)

    data class AnswerSummary(
        var propositions: List<String>,
        var answer: String
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
            question.clear()
            answers.clear()
            //todo: a simplifier
            when (subject) {
                "Science" -> {
                    val currentDao = db.quizzScienceDao()
                    val allQuestion = currentDao.getQuestion(difficulty)
                    for (i in 0..9) {
                        // question
                        question.add(allQuestion[i].question)
                        // responses
                        val currentAnswer = currentDao.getAnswers(allQuestion[i].id)
                        val currentPropositions = shuffle(currentAnswer)
                        answers.add(AnswerSummary(currentPropositions, currentAnswer.answer))
                    }
                    loading.value = false
                    return@launch
                }
                "Gastronomie" -> {
                    val currentDao = db.quizzGastronomieDao()
                    val allQuestion = currentDao.getQuestion(difficulty)
                    for (i in 0..9) {
                        // question
                        question.add(allQuestion[i].question)
                        // responses
                        val currentAnswer = currentDao.getAnswers(allQuestion[i].id)
                        val currentPropositions = shuffle(currentAnswer)
                        answers.add(AnswerSummary(currentPropositions, currentAnswer.answer))
                    }
                    loading.value = false
                    return@launch
                }
                "Géographie" -> {
                    val currentDao = db.quizzGeoDao()
                    val allQuestion = currentDao.getQuestion(difficulty)
                    for (i in 0..9) {
                        // question
                        question.add(allQuestion[i].question)
                        // responses
                        val currentAnswer = currentDao.getAnswers(allQuestion[i].id)
                        val currentPropositions = shuffle(currentAnswer)
                        answers.add(AnswerSummary(currentPropositions, currentAnswer.answer))
                    }
                    loading.value = false
                    return@launch
                }
                "Nature" -> {
                    val currentDao = db.quizzNatureDao()
                    val allQuestion = currentDao.getQuestion(difficulty)
                    for (i in 0..9) {
                        // question
                        question.add(allQuestion[i].question)
                        // responses
                        val currentAnswer = currentDao.getAnswers(allQuestion[i].id)
                        val currentPropositions = shuffle(currentAnswer)
                        answers.add(AnswerSummary(currentPropositions, currentAnswer.answer))
                    }
                    loading.value = false
                    return@launch
                }
                "Dessin anime" -> {
                    val currentDao = db.quizzDessinAnimeDao()
                    val allQuestion = currentDao.getQuestion(difficulty)
                    for (i in 0..9) {
                        // question
                        question.add(allQuestion[i].question)
                        // responses
                        val currentAnswer = currentDao.getAnswers(allQuestion[i].id)
                        val currentPropositions = shuffle(currentAnswer)
                        answers.add(AnswerSummary(currentPropositions, currentAnswer.answer))
                    }
                    loading.value = false
                    return@launch
                }
                "Informatique" -> {
                    val currentDao = db.quizzInformatiqueDao()
                    val allQuestion = currentDao.getQuestion(difficulty)
                    for (i in 0..9) {
                        // question
                        question.add(allQuestion[i].question)
                        // responses
                        val currentAnswer = currentDao.getAnswers(allQuestion[i].id)
                        val currentPropositions = shuffle(currentAnswer)
                        answers.add(AnswerSummary(currentPropositions, currentAnswer.answer))
                    }
                    loading.value = false
                    return@launch
                }
                else -> db.quizzScienceDao()
            }
        }
    }

    fun shuffle(value: AnswerSummary): List<String> {
        var randomPropositions = value.propositions.shuffled().take(4)
        while (!randomPropositions.contains(value.answer)) {
            randomPropositions = value.propositions.shuffled().take(4)
        }
        return randomPropositions.shuffled()
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