package fr.uparis.chenliu.projet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import fr.uparis.chenliu.projet.data.entity.SettingsEntity

@Dao
interface SettingsDao {

    @Insert
    suspend fun init(settings: SettingsEntity)

    @Query("SELECT * FROM SettingsEntity LIMIT 1")
    suspend fun getSetting(): SettingsEntity?

    @Update
    suspend fun updateSettings(settings: SettingsEntity)

}