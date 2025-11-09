package fr.uparis.chenliu.projet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.uparis.chenliu.projet.data.entity.AnswerHistoryEntity
import fr.uparis.chenliu.projet.data.entity.QuizzHistoryEntity
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel


@Dao
interface QuizzHistoryDao {

    @Insert
    suspend fun insertQuestion(question: QuizzHistoryEntity)

    @Insert
    suspend fun insertAnswer(answer: AnswerHistoryEntity)

//    @Query("SELECT DISTINCT * FROM QuizzHistoryEntity WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT 1")
//    suspend fun getQuestion(difficulty: String): QuizzHistoryEntity
//
//    @Query(" SELECT DISTINCT answerId, answer, validity FROM AnswerHistoryEntity WHERE idQuestion = :idQuestion AND validity = false ORDER BY RANDOM() LIMIT 3")
//    suspend fun getWrongAnswers(idQuestion: Int): List<QuizzViewModel.AnswerSummary>
//
//    @Query(" SELECT answerId, answer, validity FROM AnswerHistoryEntity WHERE idQuestion = :idQuestion AND validity = true LIMIT 1")
//    suspend fun getCorrectAnswer(idQuestion: Int): List<QuizzViewModel.AnswerSummary>


}