package fr.uparis.chenliu.projet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.uparis.chenliu.projet.data.entity.QuizzDessinAnimeEntity
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel


@Dao
interface QuizzDessinAnimeDao {


    @Insert
    suspend fun insertQuestion(question: QuizzDessinAnimeEntity)

    @Query("SELECT DISTINCT * FROM QuizzDessinAnimeEntity WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT 10")
    suspend fun getQuestion(difficulty: String): List<QuizzDessinAnimeEntity>

    @Query(" SELECT DISTINCT propositions, answer FROM QuizzDessinAnimeEntity WHERE id = :idQuestion")
    suspend fun getAnswers(idQuestion: Int): QuizzViewModel.AnswerSummary


}