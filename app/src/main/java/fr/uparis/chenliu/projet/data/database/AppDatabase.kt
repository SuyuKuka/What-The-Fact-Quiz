package fr.uparis.chenliu.projet.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.uparis.chenliu.projet.data.dao.LeaderboardDao
import fr.uparis.chenliu.projet.data.dao.QuizzDessinAnimeDao
import fr.uparis.chenliu.projet.data.dao.QuizzGeoDao

// Import DAO et Entity
import fr.uparis.chenliu.projet.data.dao.UsersDao
import fr.uparis.chenliu.projet.data.dao.SettingsDao
import fr.uparis.chenliu.projet.data.dao.QuizzScienceDao
import fr.uparis.chenliu.projet.data.dao.QuizzGastronomieDao
import fr.uparis.chenliu.projet.data.dao.QuizzInformatiqueDao
import fr.uparis.chenliu.projet.data.dao.QuizzNatureDao
import fr.uparis.chenliu.projet.data.entity.LeaderboardEntity
import fr.uparis.chenliu.projet.data.entity.QuizzDessinAnimeEntity

import fr.uparis.chenliu.projet.data.entity.QuizzGeoEntity
import fr.uparis.chenliu.projet.data.entity.QuizzGastronomieEntity
import fr.uparis.chenliu.projet.data.entity.QuizzInformatiqueEntity
import fr.uparis.chenliu.projet.data.entity.QuizzNatureEntity
import fr.uparis.chenliu.projet.data.entity.QuizzScienceEntity
import fr.uparis.chenliu.projet.data.entity.SettingsEntity
import fr.uparis.chenliu.projet.data.entity.UsersEntity

@Database(entities = [
    UsersEntity::class, SettingsEntity::class, LeaderboardEntity::class,
    QuizzScienceEntity::class, QuizzGastronomieEntity::class, QuizzGeoEntity::class,
    QuizzNatureEntity::class, QuizzInformatiqueEntity::class, QuizzDessinAnimeEntity::class],
    version = 8
)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usersDao(): UsersDao
    abstract fun settingsDao(): SettingsDao
    abstract fun leaderboardDao(): LeaderboardDao
    abstract fun quizzScienceDao(): QuizzScienceDao
    abstract fun quizzGastronomieDao(): QuizzGastronomieDao
    abstract fun quizzGeoDao(): QuizzGeoDao
    abstract fun quizzInformatiqueDao(): QuizzInformatiqueDao
    abstract fun quizzNatureDao(): QuizzNatureDao
    abstract fun quizzDessinAnimeDao(): QuizzDessinAnimeDao
    companion object {
        @Volatile
        private var dbInstance: AppDatabase? = null
        fun getInstance(c: Context): AppDatabase {
            if (dbInstance == null) {
                dbInstance = Room.databaseBuilder(
                    c.applicationContext,
                    AppDatabase::class.java, "app.db"
                ).fallbackToDestructiveMigration(true).build()
            }
            return dbInstance!!
        }
    }
}
