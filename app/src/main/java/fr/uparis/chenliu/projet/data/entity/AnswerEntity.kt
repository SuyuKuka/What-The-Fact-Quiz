package fr.uparis.chenliu.projet.data.entity

import android.R
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class AnswerEntity(
    @PrimaryKey(autoGenerate = true)
    val answerId: Int = 0,
    val idQuestion: Int,
    val answer: String,
    val validity: Boolean,
    val position: Int?
)