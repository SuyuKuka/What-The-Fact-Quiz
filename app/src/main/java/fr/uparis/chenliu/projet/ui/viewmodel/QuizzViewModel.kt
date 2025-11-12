package fr.uparis.chenliu.projet.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
        val answerHistory: MutableList<String>,
        val goodAnswer: MutableList<String>,
        val questionHistory: MutableList<String>,
        val nbQuestion: MutableIntState,
        val gameEnded: MutableState<Boolean>,
        val time: MutableDoubleState,
        val score: MutableDoubleState
    )

    private val db = AppDatabase.getInstance(application)
    private var user = mutableStateOf<UsersEntity?>(null)
    private var settings = mutableStateOf<SettingsEntity?>(null)

    // Users
    val energy = mutableIntStateOf(0)
    val subscription = mutableStateOf(false)

    // Settings
    val font = mutableIntStateOf(0)

    // Quizz
    var question = mutableStateListOf<String>()
    val answers = mutableStateListOf<AnswerSummary>()
    val buildAnswer = mutableStateListOf("", "", "", "")
    var imageAnswer = mutableStateListOf("", "", "", "")
    val loading = mutableStateOf(true)



    // loading des quiz
    val data = Data(
        listOf<String>().toMutableList(),
        listOf<AnswerSummary>().toMutableList(),
        listOf<Int>().toMutableList()
    )

    // la partie
    val gameData = GameData(
        answerHistory = mutableListOf(),
        goodAnswer = mutableListOf(),
        questionHistory = mutableListOf(),
        nbQuestion = mutableIntStateOf(10),
        gameEnded = mutableStateOf(false),
        time = mutableDoubleStateOf(0.0),
        score = mutableDoubleStateOf(0.0)
    )

    fun getQuestionAndAnswer(subject: String, difficulty: String) {
        viewModelScope.launch {
            data.question.clear()
            data.answers.clear()
            data.types.clear()
            when (subject) {
                "Science" -> { fetchScienceData(difficulty) }
                "Gastronomie" -> { fetchGastronomieData(difficulty) }
                "Géographie" -> { fetchGeographieData(difficulty) }
                "Nature" -> { fetchNatureData(difficulty) }
                "Dessin anime" -> { fetchDessinAnimeData(difficulty) }
                "Informatique" -> { fetchInformatiqueData(difficulty) }
            }
            loading.value = false
        }
    }
    suspend fun fetchScienceData(difficulty: String) {
        // on fetch les data
        val currentDao = db.quizzScienceDao()
        val allQuestion = currentDao.getQuestion(difficulty)
        for (i in 0..allQuestion.size-1) {
            // question
            data.question.add(allQuestion[i].question)
            // type
            data.types.add(allQuestion[i].type)
            // responses
            val currentAnswer = currentDao.getAnswers(allQuestion[i].id)    // pas simplifiable ya le currentdao
            val currentPropositions = shuffle(currentAnswer)
            data.answers.add(AnswerSummary(currentPropositions, currentAnswer.answer))
        }
    }
    suspend fun fetchGastronomieData(difficulty: String) {
        val currentDao = db.quizzGastronomieDao()
        val allQuestion = currentDao.getQuestion(difficulty)
        for (i in 0..allQuestion.size-1) {
            // question
            data.question.add(allQuestion[i].question)
            // type
            data.types.add(allQuestion[i].type)
            // responses
            val currentAnswer = currentDao.getAnswers(allQuestion[i].id)
            val currentPropositions = shuffle(currentAnswer)
            data.answers.add(AnswerSummary(currentPropositions, currentAnswer.answer))
        }
    }
    suspend fun fetchGeographieData(difficulty: String) {
        val currentDao = db.quizzGeoDao()
        val allQuestion = currentDao.getQuestion(difficulty)
        for (i in 0..allQuestion.size-1) {
            // question
            data.question.add(allQuestion[i].question)
            // type
            data.types.add(allQuestion[i].type)
            // responses
            val currentAnswer = currentDao.getAnswers(allQuestion[i].id)
            val currentPropositions = shuffle(currentAnswer)
            data.answers.add(AnswerSummary(currentPropositions, currentAnswer.answer))
        }
    }
    suspend fun fetchNatureData(difficulty: String) {
        val currentDao = db.quizzNatureDao()
        val allQuestion = currentDao.getQuestion(difficulty)
        for (i in 0..allQuestion.size-1) {
            // question
            data.question.add(allQuestion[i].question)
            // type
            data.types.add(allQuestion[i].type)
            // responses
            val currentAnswer = currentDao.getAnswers(allQuestion[i].id)
            val currentPropositions = shuffle(currentAnswer)
            data.answers.add(AnswerSummary(currentPropositions, currentAnswer.answer))
        }
    }
    suspend fun fetchDessinAnimeData(difficulty: String) {
        val currentDao = db.quizzDessinAnimeDao()
        val allQuestion = currentDao.getQuestion(difficulty)
        for (i in 0..allQuestion.size-1) {
            // question
            data.question.add(allQuestion[i].question)
            // type
            data.types.add(allQuestion[i].type)
            // responses
            val currentAnswer = currentDao.getAnswers(allQuestion[i].id)
            val currentPropositions =
                if (data.types[i] == 0) {
                    shuffle(currentAnswer)
                } else {
                    currentAnswer.propositions
                }
            data.answers.add(AnswerSummary(currentPropositions, currentAnswer.answer))
        }
    }
    suspend fun fetchInformatiqueData(difficulty: String) {
        val currentDao = db.quizzInformatiqueDao()
        val allQuestion = currentDao.getQuestion(difficulty)
        for (i in 0..allQuestion.size-1) {
            // question
            data.question.add(allQuestion[i].question)
            // type
            data.types.add(allQuestion[i].type)
            // responses
            val currentAnswer = currentDao.getAnswers(allQuestion[i].id)
            val currentPropositions = shuffle(currentAnswer)
            data.answers.add(AnswerSummary(currentPropositions, currentAnswer.answer))
        }
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

    fun swapAnswers(i: Int, j: Int) {
        buildAnswer[i] = buildAnswer[j].also { buildAnswer[j] = buildAnswer[i] }
        imageAnswer[i] = imageAnswer[j].also { imageAnswer[j] = imageAnswer[i] }
    }

    fun updateGame01(ans: String, index: Int): Int {
        var i = index
        // add la réponses du joueur
        gameData.answerHistory.add(ans) // add le text pos 0 -> text ; pos 1 -> lien
        gameData.questionHistory.add(data.question[i]) // connaitre les réponses de l'user
        gameData.goodAnswer.add(data.answers[i].answer[0]) // connaitre la bonne réponse
        if (ans == data.answers[i].answer[0]) { ++gameData.score.doubleValue } // si bonne réponse
        gameData.nbQuestion.intValue--      // reduire le nb de question
        gameData.gameEnded.value = gameData.nbQuestion.intValue <= 0 // regarder si la partie est fini
        if (i == 9) { i = 0 } else { i++ }
        return i
    }

    fun updateGame2(answerList: List<String>, verify: List<String>, index: Int): Int {
        var i = index
        gameData.answerHistory.add(answerList.toList().toString())
        gameData.questionHistory.add(data.question[i]) // connaitre les réponses de l'user
        gameData.goodAnswer.add(verify.toList().toString()) // connaitre la bonne réponse
        resetDragDrop()
        if (answerList == verify) { ++gameData.score.doubleValue }
        gameData.nbQuestion.intValue--      // reduire le nb de question
        gameData.gameEnded.value = gameData.nbQuestion.intValue <= 0 // regarder si la partie est fini
        if (i == 9) { i = 0 } else { i++ }
        return i
    }
    fun resetStats(gameData: GameData) {
        removeEnergy()
        gameData.nbQuestion.intValue = 10
        gameData.score.doubleValue = 0.0
        gameData.answerHistory.clear()
        gameData.questionHistory.clear()
        gameData.goodAnswer.clear()
        gameData.gameEnded.value = false
        gameData.time.doubleValue = 0.0
        resetDragDrop()
        loading.value = true
    }

    fun resetDragDrop() {
        for(i in 0..3) { buildAnswer[i] = ""; imageAnswer[i] = "" }
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