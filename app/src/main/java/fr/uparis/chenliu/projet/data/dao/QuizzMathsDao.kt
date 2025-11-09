package fr.uparis.chenliu.projet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.uparis.chenliu.projet.data.entity.AnswerMathsEntity
import fr.uparis.chenliu.projet.data.entity.QuizzMathsEntity
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel

@Dao
interface QuizzMathsDao {

    @Insert
    suspend fun insertQuestion(question: QuizzMathsEntity)

    @Insert
    suspend fun insertAnswer(answer: AnswerMathsEntity)

    @Query("SELECT DISTINCT * FROM QuizzMathsEntity WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT 1")
    suspend fun getQuestion(difficulty: String): QuizzMathsEntity

    @Query(" SELECT DISTINCT answerId, answer, validity FROM AnswerMathsEntity WHERE idQuestion = :idQuestion AND validity = false ORDER BY RANDOM() LIMIT 3")
    suspend fun getWrongAnswers(idQuestion: Int): List<QuizzViewModel.AnswerSummary>

    @Query(" SELECT answerId, answer, validity FROM AnswerMathsEntity WHERE idQuestion = :idQuestion AND validity = true LIMIT 1")
    suspend fun getCorrectAnswer(idQuestion: Int): List<QuizzViewModel.AnswerSummary>

}