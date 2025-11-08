package fr.uparis.chenliu.projet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.uparis.chenliu.projet.data.entity.AnswerEntity
import fr.uparis.chenliu.projet.data.entity.QuestionEntity
import fr.uparis.chenliu.projet.data.entity.UsersEntity

@Dao
interface QuizzDao {

    @Insert
    suspend fun insertQuestion(question: QuestionEntity)

    @Insert
    suspend fun insertAnswer(answer: AnswerEntity)

}