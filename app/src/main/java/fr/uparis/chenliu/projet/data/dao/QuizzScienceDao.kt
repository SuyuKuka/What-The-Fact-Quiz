package fr.uparis.chenliu.projet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.uparis.chenliu.projet.data.entity.QuizzScienceEntity
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel

@Dao
interface QuizzScienceDao {

    @Insert
    suspend fun insertQuestion(question: QuizzScienceEntity)

    @Query("SELECT DISTINCT * FROM QuizzScienceEntity WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT 1")
    suspend fun getQuestion(difficulty: String): QuizzScienceEntity

    @Query(" SELECT DISTINCT propositions, answer FROM QuizzScienceEntity WHERE id = :idQuestion")
    suspend fun getAnswers(idQuestion: Int): QuizzViewModel.AnswerSummary

}