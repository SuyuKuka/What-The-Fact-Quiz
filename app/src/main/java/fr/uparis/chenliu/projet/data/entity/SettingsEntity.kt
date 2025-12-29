package fr.uparis.chenliu.projet.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = UsersEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SettingsEntity(
    @PrimaryKey
    val userId: Int,
    val background: Int,
    val font: Int,
    val preferredTopics: String
)
