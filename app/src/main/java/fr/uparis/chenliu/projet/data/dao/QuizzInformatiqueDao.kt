package fr.uparis.chenliu.projet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.uparis.chenliu.projet.data.entity.QuizzGastronomieEntity
import fr.uparis.chenliu.projet.data.entity.QuizzGeoEntity
import fr.uparis.chenliu.projet.data.entity.QuizzInformatiqueEntity
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel


@Dao
interface QuizzInformatiqueDao {


    @Insert
    suspend fun insertQuestion(question: QuizzInformatiqueEntity)

    @Query("SELECT DISTINCT * FROM QuizzInformatiqueEntity WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT 1")
    suspend fun getQuestion(difficulty: String): QuizzInformatiqueEntity

    @Query(" SELECT DISTINCT propositions, answer FROM QuizzInformatiqueEntity WHERE id = :idQuestion")
    suspend fun getAnswers(idQuestion: Int): QuizzViewModel.AnswerSummary


}