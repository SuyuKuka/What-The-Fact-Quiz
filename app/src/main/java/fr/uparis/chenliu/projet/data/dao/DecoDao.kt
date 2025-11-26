package fr.uparis.chenliu.projet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import fr.uparis.chenliu.projet.data.entity.DecoEntity

@Dao
interface DecoDao {

    @Insert
    suspend fun insertDeco(deco: DecoEntity)

    @Query("SELECT name FROM DecoEntity")
    suspend fun getAllDecoNames(): List<String>

    @Query("SELECT path FROM DecoEntity")
    suspend fun getAllDecoPaths(): List<String>

    @Query("SELECT type FROM DecoEntity")
    suspend fun getAllDecoTypes(): List<Int>
}