package fr.uparis.chenliu.projet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QuizzHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val idQuestion: Int = 0,
    val difficulty: String,
    val question: String,
    val resources: String?
)