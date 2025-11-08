package fr.uparis.chenliu.projet.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import fr.uparis.chenliu.projet.R
import fr.uparis.chenliu.projet.data.dao.QuizzDao
import fr.uparis.chenliu.projet.data.model.AnswerParameter
import fr.uparis.chenliu.projet.data.model.QuestionParameter
import fr.uparis.chenliu.projet.data.database.AppDatabase
import fr.uparis.chenliu.projet.data.entity.AnswerEntity
import fr.uparis.chenliu.projet.data.entity.QuestionEntity
import fr.uparis.chenliu.projet.data.entity.SettingsEntity
import fr.uparis.chenliu.projet.data.entity.UsersEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.jvm.java

class QuizzViewModel(application: Application): AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    var currentQuestion = QuestionEntity(-1,"","","", null)

    var question = mutableStateOf("")
    val answers: Array<MutableState<QuizzDao.AnswerSummary>> = Array(5) { mutableStateOf(QuizzDao.AnswerSummary("",false)) }

    fun initQuizz(c:Context){
        viewModelScope.launch {
            val inputStream = c.resources.openRawResource(R.raw.quizz)
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            val questionData = Gson().fromJson(jsonString, QuestionParameter::class.java)
            val qData = questionData.question
            qData.forEach {
                db.quizzDao().insertQuestion(QuestionEntity(subject = it.subject, difficulty = it.difficulty, question = it.question, resources = it.resources))
            }

            val answerData = Gson().fromJson(jsonString, AnswerParameter::class.java)
            val aData = answerData.answer
            aData.forEach {
                db.quizzDao().insertAnswer(AnswerEntity(idQuestion = it.idQuestion, answer = it.answer, validity = it.validity, position = it.position ))
            }
        }
    }

    fun getQuestionAndAnswer(subject: String, difficulty: String){
        viewModelScope.launch {
            currentQuestion = db.quizzDao().getQuestion(subject, difficulty)

            // question
            question.value = currentQuestion.question

            // responses
            val bad = db.quizzDao().getWrongAnswers(currentQuestion.idQuestion)
            val good = db.quizzDao().getCorrectAnswer(currentQuestion.idQuestion)

            val res = (bad + good).shuffled()

            for (i in 0..res.size-1) {
                answers[i].value = res[i]
            }

        }
    }

//    fun checkAnswer(id: Int) {
//        viewModelScope.launch {
//            validity.value = db.quizzDao().checkValidity(id)
//        }
//    }

}