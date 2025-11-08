package fr.uparis.chenliu.projet.data.dao

import androidx.compose.runtime.MutableState
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.uparis.chenliu.projet.data.entity.AnswerEntity
import fr.uparis.chenliu.projet.data.entity.QuestionEntity
import fr.uparis.chenliu.projet.data.entity.UsersEntity
import javax.security.auth.Subject

@Dao
interface QuizzDao {

    @Insert
    suspend fun insertQuestion(question: QuestionEntity)

    @Insert
    suspend fun insertAnswer(answer: AnswerEntity)

    @Query("SELECT * FROM QuestionEntity WHERE difficulty = :difficulty AND subject = :subject ORDER BY RANDOM() LIMIT 1")
    suspend fun getQuestion(subject: Int, difficulty: Int): QuestionEntity

    @Query("SELECT answer FROM AnswerEntity WHERE idQuestion = :idQuestion")
    suspend fun getAnswersForQuestion(idQuestion: Int): List<String>

    @Query("SELECT validity FROM AnswerEntity WHERE idQuestion = :idQuestion")
    suspend fun getAnswersValidity(idQuestion: Int): Array<Boolean>

//    @Query("SELECT validity FROM AnswerEntity WHERE answerId = :answerId")
//    suspend fun checkValidity(answerId: Int): Boolean

}