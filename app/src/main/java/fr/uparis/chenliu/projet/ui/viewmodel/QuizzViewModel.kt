package fr.uparis.chenliu.projet.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import fr.uparis.chenliu.projet.data.database.AppDatabase
import kotlinx.coroutines.launch

class QuizzViewModel(application: Application): AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)

    var question = mutableStateOf("")
    val answers = mutableStateOf(AnswerSummary(listOf(),""))

    data class AnswerSummary(
        var propositions: List<String>,
        var answer: String
    )
    fun getQuestionAndAnswer(subject: String, difficulty: String) {
        viewModelScope.launch {
            when (subject) {
                "Science" -> {
                    val currentDao = db.quizzMathsDao()
                    val currentQuestion = currentDao.getQuestion(difficulty)
                    // question
                    question.value = currentQuestion.question
                    // responses
                    answers.value = currentDao.getAnswers(currentQuestion.id)
                }
//                "Histoire" -> {
//                    val currentDao = db.quizzHistoryDao()
//                    val currentQuestion = currentDao.getQuestion(difficulty)
//                    // question
//                    question.value = currentQuestion.question
//                    // responses
//                    val bad = currentDao.getWrongAnswers(currentQuestion.idQuestion)
//                    val good = currentDao.getCorrectAnswer(currentQuestion.idQuestion)
//                    val res = (bad + good).shuffled()
//                    for (i in 0..res.size - 1) {
//                        answers[i].value = res[i]
//                    }
//                }
//                "Géographie" -> {
//                    val currentDao = db.quizzGeoDao()
//                    val currentQuestion = currentDao.getQuestion(difficulty)
//                    // question
//                    question.value = currentQuestion.question
//                    // responses
//                    val bad = currentDao.getWrongAnswers(currentQuestion.idQuestion)
//                    val good = currentDao.getCorrectAnswer(currentQuestion.idQuestion)
//                    val res = (bad + good).shuffled()
//                    for (i in 0..res.size - 1) {
//                        answers[i].value = res[i]
//                    }
//                }
//                "Informatique" -> {
//                    val currentDao = db.quizzInfoDao()
//                    val currentQuestion = currentDao.getQuestion(difficulty)
//                    // question
//                    question.value = currentQuestion.question
//                    // responses
//                    val bad = currentDao.getWrongAnswers(currentQuestion.idQuestion)
//                    val good = currentDao.getCorrectAnswer(currentQuestion.idQuestion)
//                    val res = (bad + good).shuffled()
//                    for (i in 0..res.size - 1) {
//                        answers[i].value = res[i]
//                    }
//                }
                else -> db.quizzMathsDao()
            }
        }
    }
}
