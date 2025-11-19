package fr.uparis.chenliu.projet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.uparis.chenliu.projet.data.entity.LeaderboardEntity

@Dao
interface LeaderboardDao {

    @Insert
    suspend fun insertLeaderboard(leaderboard: LeaderboardEntity)

    @Query("SELECT * FROM LeaderboardEntity WHERE subject = :subject ORDER BY score DESC, time ASC LIMIT 10")
    suspend fun getLeaderboard(subject: String): MutableList<LeaderboardEntity?>

}