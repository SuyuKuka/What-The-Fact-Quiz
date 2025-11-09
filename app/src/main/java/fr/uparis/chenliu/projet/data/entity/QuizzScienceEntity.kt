package fr.uparis.chenliu.projet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QuizzScienceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val question: String,
    val propositions: List<String>,
    val answer: String,
    val difficulty: String
)