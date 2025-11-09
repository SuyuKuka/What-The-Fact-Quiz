package fr.uparis.chenliu.projet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.uparis.chenliu.projet.data.entity.QuizzGastronomieEntity
import fr.uparis.chenliu.projet.data.entity.QuizzGeoEntity
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel


@Dao
interface QuizzGastronomieDao {


    @Insert
    suspend fun insertQuestion(question: QuizzGastronomieEntity)

    @Query("SELECT DISTINCT * FROM QuizzGastronomieEntity WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT 1")
    suspend fun getQuestion(difficulty: String): QuizzGastronomieEntity

    @Query(" SELECT DISTINCT propositions, answer FROM QuizzGastronomieEntity WHERE id = :idQuestion")
    suspend fun getAnswers(idQuestion: Int): QuizzViewModel.AnswerSummary


}