package fr.uparis.chenliu.projet.data.dao

import androidx.room.Dao
import androidx.room.Insert
import fr.uparis.chenliu.projet.data.entity.SettingsEntity

@Dao
interface SettingsDao {

    @Insert
    suspend fun init(settings: SettingsEntity)



}