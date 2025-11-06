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

    @Query("DELETE FROM UsersEntity")
    suspend fun deleteAllUsers()

}