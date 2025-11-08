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

    @Query("UPDATE UsersEntity SET subscribe = :subscribe WHERE userId = :userId")
    suspend fun updateSubscribe(userId: Int, subscribe: Boolean)

    @Query("UPDATE UsersEntity SET duration = :duration WHERE userId = :userId")
    suspend fun updateDuration(userId: Int, duration: String)

    @Query("DELETE FROM UsersEntity")
    suspend fun deleteAllUsers()

}