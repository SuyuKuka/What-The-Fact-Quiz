package fr.uparis.chenliu.projet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.uparis.chenliu.projet.data.entity.SubjectEntity


    @Dao
    interface SubjectDao {

        @Insert
        suspend fun insertTheme(theme: SubjectEntity)

        @Query("SELECT imagePath FROM SubjectEntity WHERE name = :subjectName LIMIT 1")
        suspend fun getImagePathByName(subjectName: String): String?

        @Query("SELECT name FROM SubjectEntity")
        suspend fun getAllSubjectNames(): List<String>
    }