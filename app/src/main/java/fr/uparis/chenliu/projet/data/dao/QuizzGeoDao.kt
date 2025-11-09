package fr.uparis.chenliu.projet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.uparis.chenliu.projet.data.entity.AnswerGeoEntity
import fr.uparis.chenliu.projet.data.entity.QuizzGeoEntity
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel


@Dao
interface QuizzGeoDao {

    @Insert
    suspend fun insertQuestion(question: QuizzGeoEntity)

    @Insert
    suspend fun insertAnswer(answer: AnswerGeoEntity)

    @Query("SELECT DISTINCT * FROM QuizzGeoEntity WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT 1")
    suspend fun getQuestion(difficulty: String): QuizzGeoEntity

    @Query(" SELECT DISTINCT answerId, answer, validity FROM AnswerGeoEntity WHERE idQuestion = :idQuestion AND validity = false ORDER BY RANDOM() LIMIT 3")
    suspend fun getWrongAnswers(idQuestion: Int): List<QuizzViewModel.AnswerSummary>

    @Query(" SELECT answerId, answer, validity FROM AnswerGeoEntity WHERE idQuestion = :idQuestion AND validity = true LIMIT 1")
    suspend fun getCorrectAnswer(idQuestion: Int): List<QuizzViewModel.AnswerSummary>


}