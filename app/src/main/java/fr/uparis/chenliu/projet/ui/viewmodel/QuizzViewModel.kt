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
                    val currentDao = db.quizzScienceDao()
                    val currentQuestion = currentDao.getQuestion(difficulty)
                    // question
                    question.value = currentQuestion.question
                    // responses
                    answers.value = currentDao.getAnswers(currentQuestion.id)
                }
                "Gastronomie" -> {
                    val currentDao = db.quizzGastronomieDao()
                    val currentQuestion = currentDao.getQuestion(difficulty)
                    // question
                    question.value = currentQuestion.question
                    // responses
                    answers.value = currentDao.getAnswers(currentQuestion.id)
                }
                "Géographie" -> {
                    val currentDao = db.quizzGeoDao()
                    val currentQuestion = currentDao.getQuestion(difficulty)
                    // question
                    question.value = currentQuestion.question
                    // responses
                    answers.value = currentDao.getAnswers(currentQuestion.id)
                }
                else -> db.quizzScienceDao()
            }
        }
    }
}
