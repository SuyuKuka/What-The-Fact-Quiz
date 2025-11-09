package fr.uparis.chenliu.projet.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.uparis.chenliu.projet.data.dao.QuizzGeoDao

// Import DAO et Entity
import fr.uparis.chenliu.projet.data.dao.UsersDao
import fr.uparis.chenliu.projet.data.dao.SettingsDao
import fr.uparis.chenliu.projet.data.dao.QuizzMathsDao
import fr.uparis.chenliu.projet.data.dao.QuizzHistoryDao
import fr.uparis.chenliu.projet.data.dao.QuizzInfoDao
import fr.uparis.chenliu.projet.data.entity.AnswerGeoEntity
import fr.uparis.chenliu.projet.data.entity.AnswerHistoryEntity
import fr.uparis.chenliu.projet.data.entity.AnswerInfoEntity

import fr.uparis.chenliu.projet.data.entity.AnswerMathsEntity
import fr.uparis.chenliu.projet.data.entity.QuizzGeoEntity
import fr.uparis.chenliu.projet.data.entity.QuizzHistoryEntity
import fr.uparis.chenliu.projet.data.entity.QuizzInfoEntity
import fr.uparis.chenliu.projet.data.entity.QuizzScienceEntity
import fr.uparis.chenliu.projet.data.entity.SettingsEntity
import fr.uparis.chenliu.projet.data.entity.UsersEntity

@Database(entities = [
    UsersEntity::class, SettingsEntity::class,
    AnswerMathsEntity::class, QuizzScienceEntity::class,
    AnswerHistoryEntity::class, QuizzHistoryEntity::class,
    AnswerInfoEntity::class, QuizzInfoEntity::class,
    AnswerGeoEntity::class, QuizzGeoEntity::class], version = 3 )
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usersDao(): UsersDao
    abstract fun settingsDao(): SettingsDao
    abstract fun quizzMathsDao(): QuizzMathsDao
    abstract fun quizzHistoryDao(): QuizzHistoryDao
    abstract fun quizzGeoDao(): QuizzGeoDao
    abstract fun quizzInfoDao(): QuizzInfoDao
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
