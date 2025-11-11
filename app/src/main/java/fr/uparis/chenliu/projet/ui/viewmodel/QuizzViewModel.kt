package fr.uparis.chenliu.projet.ui.viewmodel

import android.R.attr.data
import android.R.attr.type
import android.app.Application
import android.util.Log.i
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.uparis.chenliu.projet.R

import fr.uparis.chenliu.projet.data.database.AppDatabase
import fr.uparis.chenliu.projet.data.entity.SettingsEntity
import fr.uparis.chenliu.projet.data.entity.UsersEntity
import kotlinx.coroutines.launch
import kotlin.collections.mutableListOf
import kotlin.math.abs

class QuizzViewModel(application: Application): AndroidViewModel(application) {

    data class AnswerSummary(
        var propositions: List<List<String>>,
        var answer: List<String>
    )

    data class Data(    // les info récupéré depuis la db
        val question : MutableList<String>,
        val answers: MutableList<AnswerSummary>,
        val types : MutableList<Int>
    )

    data class GameData(    // les info pendant la partie
        val answerHistory : MutableList<String>,
        val goodAnswer : MutableList<String>,
        val questionHistory : MutableList<String>,
        val nbQuestion : MutableIntState,
    )

    private val db = AppDatabase.getInstance(application)
    private var user = mutableStateOf<UsersEntity?>(null)
    private var settings = mutableStateOf<SettingsEntity?>(null)

    // autour de la partie
    val energy = mutableIntStateOf(0)
    val subscription = mutableStateOf(false)
    val font = mutableIntStateOf(0)
    val score = mutableDoubleStateOf(0.0)


    // loading des quiz
    var question = mutableListOf<String>()
    val answers = mutableListOf<AnswerSummary>()
    val types = mutableListOf<Int>()

    val answerHistory = mutableListOf<String>()
    val goodAnswer = mutableListOf<String>()
    val questionHistory = mutableListOf<String>()
    val nbQuestion = mutableIntStateOf(10)

    val loading = mutableStateOf(true)



    fun getQuestionAndAnswer(subject: String, difficulty: String) {
        viewModelScope.launch {
            question.clear()
            answers.clear()
            types.clear()
            when (subject) {
                "Science" -> { fetchScienceData(difficulty); return@launch }
                "Gastronomie" -> { fetchGastronomieData(difficulty); return@launch }
                "Géographie" -> { fetchGeographieData(difficulty); return@launch }
                "Nature" -> { fetchNatureData(difficulty); return@launch }
                "Dessin anime" -> { fetchDessinAnimeData(difficulty); return@launch }
                "Informatique" -> { fetchInformatiqueData(difficulty); return@launch }
            }
        }
    }
    suspend fun fetchScienceData(difficulty: String) {
        // on fetch les data
        val currentDao = db.quizzScienceDao()
        val allQuestion = currentDao.getQuestion(difficulty)
        for (i in 0..allQuestion.size-1) {
            // question
            question.add(allQuestion[i].question)
            // type
            types.add(allQuestion[i].type)
            // responses
            val currentAnswer = currentDao.getAnswers(allQuestion[i].id)    // pas simplifiable ya le currentdao
            val currentPropositions = shuffle(currentAnswer)
            answers.add(AnswerSummary(currentPropositions, currentAnswer.answer))
        }
        loading.value = false
    }
    suspend fun fetchGastronomieData(difficulty: String) {
        val currentDao = db.quizzGastronomieDao()
        val allQuestion = currentDao.getQuestion(difficulty)
        for (i in 0..allQuestion.size-1) {
            // question
            question.add(allQuestion[i].question)
            // type
            types.add(allQuestion[i].type)
            // responses
            val currentAnswer = currentDao.getAnswers(allQuestion[i].id)
            val currentPropositions = shuffle(currentAnswer)
            answers.add(AnswerSummary(currentPropositions, currentAnswer.answer))
        }
        loading.value = false
    }
    suspend fun fetchGeographieData(difficulty: String) {
        val currentDao = db.quizzGeoDao()
        val allQuestion = currentDao.getQuestion(difficulty)
        for (i in 0..allQuestion.size-1) {
            // question
            question.add(allQuestion[i].question)
            // type
            types.add(allQuestion[i].type)
            // responses
            val currentAnswer = currentDao.getAnswers(allQuestion[i].id)
            val currentPropositions = shuffle(currentAnswer)
            answers.add(AnswerSummary(currentPropositions, currentAnswer.answer))
        }
        loading.value = false
    }
    suspend fun fetchNatureData(difficulty: String) {
        val currentDao = db.quizzNatureDao()
        val allQuestion = currentDao.getQuestion(difficulty)
        for (i in 0..allQuestion.size-1) {
            // question
            question.add(allQuestion[i].question)
            // type
            types.add(allQuestion[i].type)
            // responses
            val currentAnswer = currentDao.getAnswers(allQuestion[i].id)
            val currentPropositions = shuffle(currentAnswer)
            answers.add(AnswerSummary(currentPropositions, currentAnswer.answer))
        }
        loading.value = false
    }
    suspend fun fetchDessinAnimeData(difficulty: String) {
        val currentDao = db.quizzDessinAnimeDao()
        val allQuestion = currentDao.getQuestion(difficulty)
        for (i in 0..allQuestion.size-1) {
            // question
            question.add(allQuestion[i].question)
            // type
            types.add(allQuestion[i].type)
            // responses
            val currentAnswer = currentDao.getAnswers(allQuestion[i].id)
            val currentPropositions =
                if (types[i] == 0) {
                    println(types)
                    println(types)
                    println(types)
                    println(types)
                    shuffle(currentAnswer)
                } else {
                    currentAnswer.propositions
                }
            answers.add(AnswerSummary(currentPropositions, currentAnswer.answer))
        }
        loading.value = false
    }
    suspend fun fetchInformatiqueData(difficulty: String) {
        val currentDao = db.quizzInformatiqueDao()
        val allQuestion = currentDao.getQuestion(difficulty)
        for (i in 0..allQuestion.size-1) {
            // question
            question.add(allQuestion[i].question)
            // type
            types.add(allQuestion[i].type)
            // responses
            val currentAnswer = currentDao.getAnswers(allQuestion[i].id)
            val currentPropositions = shuffle(currentAnswer)
            answers.add(AnswerSummary(currentPropositions, currentAnswer.answer))
        }
        loading.value = false
    }

    // permet de rendre les propositions aléatoire
    fun shuffle(value: AnswerSummary): List<List<String>> {
        var randomPropositions = value.propositions.shuffled().take(4) // prop [[e,""],[e,""]]
        var shuffling = true
        while (shuffling) {
   
            randomPropositions = value.propositions.shuffled().take(4)
            for (p in randomPropositions) {
                if (p[0] == value.answer[0]) {
                    shuffling = false   // answer fait partie des proposition
                }
            }// si ya pas la réponse parmis les propositions
        }
        return randomPropositions.shuffled()
    }

    // todo: améliorer
    fun calcScore(text: String, aim: String): Double {
        if (abs(aim.toInt() - text.toInt()) <= 1) {
            return 0.5
        }
        return 0.0
    }

    fun updateGame(ans: String, index: Int): Int {
        var i = index
        // add la réponses du joueur
        answerHistory.add(ans) // add le text pos 0 -> text ; pos 1 -> lien
        questionHistory.add(question[i])
        goodAnswer.add(answers[i].answer[0])
        if (ans == answers[i].answer[0]) { ++score.doubleValue }
        nbQuestion.intValue--
        if (i == 9) { i == 0 } else { i++ }
        return i
    }
    fun resetStats(gameData: GameData) {
        removeEnergy()
        gameData.nbQuestion.intValue = 10
        score.doubleValue = 0.0
        gameData.answerHistory.clear()
        gameData.questionHistory.clear()
        gameData.goodAnswer.clear()
        loading.value = true
    }

    fun init() {
        viewModelScope.launch {
            user.value = db.usersDao().getUser()
            energy.intValue = user.value!!.energy
            subscription.value = user.value!!.subscribe

            settings.value = db.settingsDao().getSetting()
            font.intValue = settings.value!!.font
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

    fun getUsers() {
        viewModelScope.launch {
            user.value = db.usersDao().getUser()
            energy.intValue = user.value!!.energy
            subscription.value = user.value!!.subscribe
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