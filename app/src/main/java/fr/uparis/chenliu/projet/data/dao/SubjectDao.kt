package fr.uparis.chenliu.projet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.uparis.chenliu.projet.data.entity.SubjectEntity

@Dao
interface SubjectDao {

    @Insert
    suspend fun insertTheme(theme: SubjectEntity)

    @Query("SELECT imageName FROM SubjectEntity")
    suspend fun getAllImageNames(): List<String>

    @Query("SELECT name FROM SubjectEntity")
    suspend fun getAllSubjectNames(): List<String>
}