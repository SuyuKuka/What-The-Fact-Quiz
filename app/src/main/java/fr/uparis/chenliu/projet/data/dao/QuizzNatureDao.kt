package fr.uparis.chenliu.projet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.uparis.chenliu.projet.data.entity.QuizzNatureEntity
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel


@Dao
interface QuizzNatureDao {


    @Insert
    suspend fun insertQuestion(question: QuizzNatureEntity)

    @Query("SELECT DISTINCT * FROM QuizzNatureEntity WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT 10")
    suspend fun getQuestion(difficulty: String): List<QuizzNatureEntity>

    @Query(" SELECT DISTINCT propositions, answer FROM QuizzNatureEntity WHERE id = :idQuestion")
    suspend fun getAnswers(idQuestion: Int): QuizzViewModel.AnswerSummary


}