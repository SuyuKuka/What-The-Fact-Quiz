package fr.uparis.chenliu.projet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QuizzDessinAnimeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: Int,
    val question: String,
    val propositions: List<List<String>>,
    val answer: List<String>,
    val difficulty: String
)