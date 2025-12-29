package fr.uparis.chenliu.projet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.uparis.chenliu.projet.data.entity.QuizzEntity
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel

@Dao
interface QuizzDao {

    @Insert
    suspend fun insertQuestion(question: QuizzEntity)

    @Query(
        """
    SELECT DISTINCT q.* 
    FROM QuizzEntity q
    INNER JOIN SubjectEntity s ON q.subject = s.id
    WHERE q.difficulty = :difficulty AND s.name = :subject
    ORDER BY RANDOM()
    LIMIT 10
"""
    )
    suspend fun getQuestion(difficulty: String, subject: String): List<QuizzEntity>

    @Query(" SELECT DISTINCT propositions, answer FROM QuizzEntity WHERE id = :idQuestion")
    suspend fun getAnswers(idQuestion: Int): QuizzViewModel.AnswerSummary


}