package fr.uparis.chenliu.projet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.uparis.chenliu.projet.data.entity.UsersEntity

@Dao
interface UsersDao {

    @Insert
    suspend fun insertUser(user: UsersEntity)

    @Query("SELECT EXISTS(SELECT * FROM UsersEntity)")
    suspend fun isExist(): Boolean

    @Query("SELECT * FROM UsersEntity LIMIT 1")
    suspend fun getUser(): UsersEntity?

    @Query("UPDATE UsersEntity SET username = :username WHERE userId = :userId")
    suspend fun updateUsername(userId: Int, username: String)

    @Query("UPDATE UsersEntity SET subscribe = :subscribe WHERE userId = :userId")
    suspend fun updateSubscribe(userId: Int, subscribe: Boolean)

    @Query("UPDATE UsersEntity SET endDate = :endDate WHERE userId = :userId")
    suspend fun updateDuration(userId: Int, endDate: String)

    @Query("UPDATE UsersEntity SET energy = :energy, lastTimeEnergy = :lastTimeEnergy WHERE userId = :userId")
    suspend fun updateEnergy(userId: Int, energy: Int, lastTimeEnergy: Long)

    @Query("DELETE FROM UsersEntity")
    suspend fun deleteAllUsers()

}