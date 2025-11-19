package fr.uparis.chenliu.projet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.uparis.chenliu.projet.data.entity.QuizzGastronomieEntity
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel


@Dao
interface QuizzGastronomieDao {


    @Insert
    suspend fun insertQuestion(question: QuizzGastronomieEntity)

    @Query("SELECT DISTINCT * FROM QuizzGastronomieEntity WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT 10")
    suspend fun getQuestion(difficulty: String): List<QuizzGastronomieEntity>

    @Query(" SELECT DISTINCT propositions, answer FROM QuizzGastronomieEntity WHERE id = :idQuestion")
    suspend fun getAnswers(idQuestion: Int): QuizzViewModel.AnswerSummary


}