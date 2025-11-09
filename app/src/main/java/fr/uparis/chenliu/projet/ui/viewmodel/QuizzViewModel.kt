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
import fr.uparis.chenliu.projet.data.entity.AnswerHistoryEntity
import fr.uparis.chenliu.projet.data.entity.AnswerMathsEntity
import fr.uparis.chenliu.projet.data.entity.QuizzHistoryEntity
import fr.uparis.chenliu.projet.data.entity.QuizzMathsEntity
import kotlinx.coroutines.launch
import kotlin.jvm.java

class QuizzViewModel(application: Application): AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)

    var question = mutableStateOf("")
    val answers: Array<MutableState<AnswerSummary>> = Array(4) { mutableStateOf(AnswerSummary("",false)) }

    data class AnswerSummary(
        var answer: String,
        var validity: Boolean
    )

    fun initQuizz(c:Context){
        viewModelScope.launch {
            var inputStream = c.resources.openRawResource(R.raw.quizzmaths)
            var jsonString = inputStream.bufferedReader().use { it.readText() }
            var questionData = Gson().fromJson(jsonString, QuestionParameter::class.java)

            var qData = questionData.question
            qData.forEach {
                db.quizzMathsDao().insertQuestion(QuizzMathsEntity( difficulty = it.difficulty, question = it.question, resources = it.resources))
            }
            var answerData = Gson().fromJson(jsonString, AnswerParameter::class.java)
            var aData = answerData.answer
            aData.forEach {
                db.quizzMathsDao().insertAnswer(AnswerMathsEntity(idQuestion = it.idQuestion, answer = it.answer, validity = it.validity, position = it.position ))
            }

            inputStream = c.resources.openRawResource(R.raw.quizzhistory)
            jsonString = inputStream.bufferedReader().use { it.readText() }
            questionData = Gson().fromJson(jsonString, QuestionParameter::class.java)

            qData = questionData.question
            qData.forEach {
                db.quizzHistoryDao().insertQuestion(QuizzHistoryEntity( difficulty = it.difficulty, question = it.question, resources = it.resources))
            }
            answerData = Gson().fromJson(jsonString, AnswerParameter::class.java)
            aData = answerData.answer
            aData.forEach {
                db.quizzHistoryDao().insertAnswer(AnswerHistoryEntity(idQuestion = it.idQuestion, answer = it.answer, validity = it.validity, position = it.position ))
            }
        }
    }



    fun getQuestionAndAnswer(subject: String, difficulty: String){
        viewModelScope.launch {
            when(subject) {
                "Maths" -> {
                    val currentDao = db.quizzMathsDao()
                    val currentQuestion = currentDao.getQuestion(difficulty)

                    // question
                    question.value = currentQuestion.question
                    // responses
                    val bad = currentDao.getWrongAnswers(currentQuestion.idQuestion)
                    val good = currentDao.getCorrectAnswer(currentQuestion.idQuestion)
                    val res = (bad + good).shuffled()

                    Log.d("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", currentQuestion.idQuestion.toString())

                    for (i in 0..res.size-1) {
                        answers[i].value= res[i]
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
                    for (i in 0..res.size-1) {
                        answers[i].value= res[i]
                    }
                }
                else -> db.quizzMathsDao()
            }
        }
    }

//    fun checkAnswer(id: Int) {
//        viewModelScope.launch {
//            validity.value = db.quizzDao().checkValidity(id)
//        }
//    }

}