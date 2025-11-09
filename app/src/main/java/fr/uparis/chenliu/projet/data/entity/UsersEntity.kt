package fr.uparis.chenliu.projet.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UsersEntity (
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val username: String,
    val energy: Int,
    val lastTimeEnergy: Long,
    val subscribe: Boolean,
    val endDate: String,
    val games: Int,
    val wins: Int
)