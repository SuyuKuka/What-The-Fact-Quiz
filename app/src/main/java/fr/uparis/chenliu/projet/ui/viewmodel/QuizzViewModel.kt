package fr.uparis.chenliu.projet.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import fr.uparis.chenliu.projet.R
import fr.uparis.chenliu.projet.data.dao.QuizzMathsDao
import fr.uparis.chenliu.projet.data.model.AnswerParameter
import fr.uparis.chenliu.projet.data.model.QuestionParameter
import fr.uparis.chenliu.projet.data.database.AppDatabase
import fr.uparis.chenliu.projet.data.entity.AnswerGeoEntity
import fr.uparis.chenliu.projet.data.entity.AnswerHistoryEntity
import fr.uparis.chenliu.projet.data.entity.AnswerInfoEntity
import fr.uparis.chenliu.projet.data.entity.AnswerMathsEntity
import fr.uparis.chenliu.projet.data.entity.QuizzGeoEntity
import fr.uparis.chenliu.projet.data.entity.QuizzHistoryEntity
import fr.uparis.chenliu.projet.data.entity.QuizzInfoEntity
import fr.uparis.chenliu.projet.data.entity.QuizzMathsEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.jvm.java

class QuizzViewModel(application: Application): AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)

    var question = mutableStateOf("")
    val answers: Array<MutableState<AnswerSummary>> = Array(4) { mutableStateOf(AnswerSummary(-1,"",false)) }

    data class AnswerSummary(
        var answerId: Int,
        var answer: String,
        var validity: Boolean
    )
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
}
