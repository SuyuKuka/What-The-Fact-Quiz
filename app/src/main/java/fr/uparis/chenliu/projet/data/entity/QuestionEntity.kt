package fr.uparis.chenliu.projet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val idQuestion: Int = 0,
    val subject: Int,
    val difficulty: Int,
    val question: String,
    val resources: String?
)