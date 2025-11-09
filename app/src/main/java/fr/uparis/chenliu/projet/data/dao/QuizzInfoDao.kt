package fr.uparis.chenliu.projet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.uparis.chenliu.projet.data.entity.AnswerInfoEntity
import fr.uparis.chenliu.projet.data.entity.QuizzInfoEntity
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel


@Dao
interface QuizzInfoDao {

    @Insert
    suspend fun insertQuestion(question: QuizzInfoEntity)

    @Insert
    suspend fun insertAnswer(answer: AnswerInfoEntity)

    @Query("SELECT DISTINCT * FROM QuizzInfoEntity WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT 1")
    suspend fun getQuestion(difficulty: String): QuizzInfoEntity

    @Query(" SELECT DISTINCT answerId, answer, validity FROM AnswerInfoEntity WHERE idQuestion = :idQuestion AND validity = false ORDER BY RANDOM() LIMIT 3")
    suspend fun getWrongAnswers(idQuestion: Int): List<QuizzViewModel.AnswerSummary>

    @Query(" SELECT answerId, answer, validity FROM AnswerInfoEntity WHERE idQuestion = :idQuestion AND validity = true LIMIT 1")
    suspend fun getCorrectAnswer(idQuestion: Int): List<QuizzViewModel.AnswerSummary>


}