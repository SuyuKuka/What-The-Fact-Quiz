package fr.uparis.chenliu.projet.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subject"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class QuizzEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: Int,
    val question: String,
    val propositions: List<List<String>>,
    val answer: List<String>,
    val difficulty: String,
    val subject: Int
)