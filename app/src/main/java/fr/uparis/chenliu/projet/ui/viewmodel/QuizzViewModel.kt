package fr.uparis.chenliu.projet.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.uparis.chenliu.projet.data.database.AppDatabase
import fr.uparis.chenliu.projet.data.entity.LeaderboardEntity
import fr.uparis.chenliu.projet.data.entity.SettingsEntity
import fr.uparis.chenliu.projet.data.entity.UsersEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.collections.mutableListOf

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
        val score: MutableIntState
    )

    private val db = AppDatabase.getInstance(application)
    private var user = mutableStateOf<UsersEntity?>(null)
    private var settings = mutableStateOf<SettingsEntity?>(null)
    private var chronoJob: Job? = null

    // Users
    val energy = mutableIntStateOf(0)
    val subscription = mutableStateOf(false)

    // Settings
    val backgroundList = MutableStateFlow<List<String>>(emptyList())
    val fontList = MutableStateFlow<List<String>>(emptyList())
    val font = mutableIntStateOf(0)
    val background = mutableIntStateOf(0)
    val currentFont = mutableStateOf(listOf(0, 0))
    val currentBackground = mutableIntStateOf(0)

    val decoNameList = MutableStateFlow<List<String>>(emptyList())
    val decoPathList = MutableStateFlow<List<String>>(emptyList())
    val decoTypeList = MutableStateFlow<List<Int>>(emptyList())
    val decoDiffList = MutableStateFlow<List<Int>>(emptyList())

    // Quizz
    val subject = mutableStateOf("")
    var question = mutableStateListOf<String>()
    val textFieldAnswer = mutableStateOf("")
    var buildAnswer = mutableStateListOf("", "", "", "")
    var imageAnswer = mutableStateListOf("", "", "", "")
    val currentQuestion = mutableIntStateOf(0)
    val currentImage = mutableIntStateOf(0)

    // Others
    val loading = mutableStateOf(true)

    // loading des quiz
    val data = mutableStateOf(Data(
        question = listOf<String>().toMutableList(),
        answers = listOf<AnswerSummary>().toMutableList(),
        types = listOf<Int>().toMutableList()
    ))

    // la partie
    val gameData = GameData(
        answerHistory = mutableListOf(),
        goodAnswer = mutableListOf(),
        questionHistory = mutableListOf(),
        nbQuestion = mutableIntStateOf(10),
        gameEnded = mutableStateOf(false),
        time = mutableDoubleStateOf(0.0),
        score = mutableIntStateOf(0)
    )

    fun getQuestionAndAnswer(subject: String, difficulty: String) {
        this.subject.value = subject
        viewModelScope.launch {
            data.value.question.clear()
            data.value.answers.clear()
            data.value.types.clear()

            fetchQuizzData(difficulty, subject)
            resetDragDrop()
            loading.value = false
        }
        chronometer()
    }

    suspend fun fetchQuizzData(difficulty: String, subject: String) {
        // on fetch les data
        val currentDao = db.quizzDao()
        val allQuestion = currentDao.getQuestion(difficulty, subject)
        val newQuestions = mutableListOf<String>()
        val newAnswers = mutableListOf<AnswerSummary>()
        val newTypes = mutableListOf<Int>()

        for (i in allQuestion.indices) {
            // question
            newQuestions.add(allQuestion[i].question)
            // type
            newTypes.add(allQuestion[i].type)
            // responses
            val currentAnswer = currentDao.getAnswers(allQuestion[i].id)
            val currentPropositions =
                if (allQuestion[i].type == 0) {
                    shuffle(currentAnswer)
                } else {
                    currentAnswer.propositions
                }
            newAnswers.add(AnswerSummary(currentPropositions, currentAnswer.answer))
        }

        data.value = Data(
            question = newQuestions.toMutableList(),
            answers = newAnswers.toMutableList(),
            types = newTypes.toMutableList()
        )
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

    fun calcScore(gameData: GameData, answerList: List<String>, goodAnswers: List<String>, type: Int) {
        val score = mutableIntStateOf(0)
        if (type == 0 || type == 3) {
            if (answerList.toList().toString().lowercase() == goodAnswers.toList().toString().lowercase()){
                score.intValue = score.intValue + 1000
            }
        } else if (type == 1) {
            score.intValue = score.intValue + approxScore(answer = answerList.toList()[0].lowercase(), goodAnswer = goodAnswers.toList()[0].lowercase())
        } else if (type == 2) {
            // c'est plus long, et plus compliqué d'avoir la réponse exacte, donc les type 2, 4 valent 4000 au total
            for (i in 0..goodAnswers.size - 1) {
                if (answerList.toList()[i].lowercase() == goodAnswers.toList()[i].lowercase()) {
                    score.intValue = score.intValue + 1000
                }
            }
        } else if (type == 4) {
            if (answerList.toList() == goodAnswers.toList()) {
                score.intValue = score.intValue + 1000
            }
        }
        gameData.score.intValue +=  score.intValue
    }

    fun approxScore(answer: String, goodAnswer: String): Int {
        // si answer et guess sont des nombre
        if (answer.toIntOrNull() != null && goodAnswer.toIntOrNull() != null) {
            val a = answer.toDouble()
            val g = goodAnswer.toDouble()
            val minVal = minOf(a, g)
            val maxVal = maxOf(a, g)
            val ratio = if (maxVal == 0.0) 1.0 else minVal / maxVal
            return (ratio * 1000).toInt()
        } else {
            val size = minOf(answer.length, goodAnswer.length)
            var correct = 0
            for (i in 0..size-1) {
                if (answer[i] == goodAnswer[i]) correct++
            }
            // nb de lettre correcte sur le nb de lettre de la réponse
            val percent = (correct.toDouble() / maxOf(answer.length, goodAnswer.length)) * 100
            val score = (percent / 100) * 1000
            return score.toInt()
        }
    }

    fun swapAnswers(indexAnswer: Int, indexImage: Int) {
        buildAnswer[indexImage] = buildAnswer[indexAnswer].also { buildAnswer[indexAnswer] = buildAnswer[indexImage] }
        imageAnswer[indexImage] = imageAnswer[indexAnswer].also { imageAnswer[indexAnswer] = imageAnswer[indexImage] }
    }

    fun updateGame(answerList: List<String>, goodAnswers: List<String>, type: Int) {
        gameData.answerHistory.add(answerList.toList().toString())
        gameData.questionHistory.add(data.value.question[currentQuestion.intValue]) // connaitre les réponses de l'user
        gameData.goodAnswer.add(goodAnswers.toList().toString()) // connaitre la bonne réponse
        calcScore(gameData = gameData, answerList = answerList, goodAnswers = goodAnswers, type = type)
        gameData.nbQuestion.intValue--      // reduire le nb de question
        if (gameData.nbQuestion.intValue <= 0) { // regarder si la partie est fini
            gameData.gameEnded.value = true
            stopChronometer()
        }
        if (currentQuestion.intValue == 9) { currentQuestion.intValue = 0 } else { currentQuestion.intValue++ }
        resetDragDrop()
        textFieldAnswer.value = ""
    }

    fun resetStats(gameData: GameData) {
        removeEnergy()
        gameData.nbQuestion.intValue = 10
        gameData.score.intValue = 0
        gameData.answerHistory.clear()
        gameData.questionHistory.clear()
        gameData.goodAnswer.clear()
        gameData.gameEnded.value = false
        gameData.time.doubleValue = 0.0
        textFieldAnswer.value = ""
        resetDragDrop()
        currentQuestion.intValue = 0
        currentImage.intValue = 0
        loading.value = true
    }

    fun resetDragDrop() {
        val type = data.value.types[currentQuestion.intValue]
        for(i in 0..buildAnswer.size - 1) { buildAnswer[i] = ""; imageAnswer[i] = "" }
        when (type) {
            3 -> {
                for (i in 0..2) {
                    buildAnswer[i] = data.value.answers[currentQuestion.intValue].propositions[i][0]
                    imageAnswer[i] = data.value.answers[currentQuestion.intValue].propositions[i][1]
                }
                currentImage.intValue = 3
            }
            4 -> {
                buildAnswer[0] = data.value.answers[currentQuestion.intValue].propositions[0][0]
                imageAnswer[0] = data.value.answers[currentQuestion.intValue].propositions[0][1]
                currentImage.intValue = 1
            }
            else -> {
                currentImage.intValue = 0
            }
        }
    }

    fun replaceAnswer(answer: String, image: String, indexAnswer: Int) {
        if (buildAnswer.contains(answer)) {
            for (i in 0..3) {
                if (buildAnswer[i] == answer) {
                    buildAnswer[i] = ""
                    imageAnswer[i] = ""
                    break
                }
            }
        }
        buildAnswer[indexAnswer] = answer
        imageAnswer[indexAnswer] = image
        currentImage.intValue++
    }

    fun insertAnswer(answer: String, image: String, indexAnswer: Int, quizzType: Int) {
        if (indexAnswer <= buildAnswer.size) {
            for (i in 0..buildAnswer.size - 2 - indexAnswer) {
                buildAnswer[buildAnswer.size - 1 - i] = buildAnswer[buildAnswer.size - 2 - i]
                imageAnswer[imageAnswer.size - 1 - i] = imageAnswer[imageAnswer.size - 2 - i]
            }
            buildAnswer[indexAnswer] = answer
            imageAnswer[indexAnswer] = image
            currentImage.intValue++

            if (quizzType == 4) {
                val goodAnswers = mutableStateListOf<String>()
                val goodImage = mutableStateListOf<String>()
                for (i in 0..data.value.answers[currentQuestion.intValue].answer.size - 1) {
                    if (buildAnswer.contains(data.value.answers[currentQuestion.intValue].answer[i])) {
                        goodAnswers.add(data.value.answers[currentQuestion.intValue].answer[i])
                    }
                }
                for (i in 0..goodAnswers.size - 1) {
                    for (j in 0.. buildAnswer.size - 1) {
                        if (buildAnswer[j] == goodAnswers[i]) {
                            goodImage.add(imageAnswer[j])
                            break
                        }
                    }
                }
                val count = buildAnswer.size - goodAnswers.size
                repeat(count) {
                    goodAnswers.add("")
                    goodImage.add("")
                }

                calcScore(gameData = gameData, answerList = buildAnswer, goodAnswers = goodAnswers, type = 4)

                buildAnswer = goodAnswers
                imageAnswer = goodImage
            }
        }
    }

    fun init(context: Context) {
        viewModelScope.launch {
            user.value = db.usersDao().getUser()
            energy.intValue = user.value!!.energy
            subscription.value = user.value!!.subscribe

            settings.value = db.settingsDao().getSetting()
            font.intValue = settings.value!!.font
            background.intValue = settings.value!!.background
            decoNameList.value = db.decoDao().getAllDecoNames()
            decoPathList.value = db.decoDao().getAllDecoPaths()
            decoTypeList.value = db.decoDao().getAllDecoTypes()
            decoDiffList.value = db.decoDao().getAllDecoDiff()
            for (i in 0..decoTypeList.value.size - 1) {
                if (decoTypeList.value[i] == 0) {
                    backgroundList.value += decoNameList.value[i]
                } else {
                    fontList.value += decoNameList.value[i]
                }
            }
            getCurrentFontAndBackground(context)
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

    @SuppressLint("DiscouragedApi", "AutoboxingStateValueProperty")
    fun getCurrentFontAndBackground(context: Context) {
        if (decoPathList.value.isEmpty()) {
            currentFont.value = listOf(0, 0)
            return
        }
        val resources = context.resources
        val fontId = resources.getIdentifier(
            decoPathList.value[font.intValue + backgroundList.value.size],
            "font",
            context.packageName
        )
        val fontDiff = decoDiffList.value[font.intValue + backgroundList.value.size]
        currentFont.value = listOf(fontId, fontDiff)
        val backgroundId = resources.getIdentifier(
            decoPathList.value[background.intValue],
            "drawable",
            context.packageName
        )
        currentBackground.intValue = backgroundId
    }

    fun insertLeaderboard() {
        viewModelScope.launch {
            db.leaderboardDao().insertLeaderboard(LeaderboardEntity(userId = user.value!!.userId, score = gameData.score.intValue, time = gameData.time.doubleValue, subject = subject.value))
        }
    }

    fun chronometer() {
        chronoJob?.cancel()
        chronoJob = viewModelScope.launch {
            val start = System.currentTimeMillis()
            while (isActive && !gameData.gameEnded.value) {
                val now = System.currentTimeMillis()
                val diff = now - start
                gameData.time.doubleValue = diff / 1000.0
                delay(50L)
            }
        }
    }

    // pour eviter plusieur coroutine
    fun stopChronometer() {
        chronoJob?.cancel()
        chronoJob = null
    }

}