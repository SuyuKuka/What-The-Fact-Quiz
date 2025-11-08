package fr.uparis.chenliu.projet.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Import DAO et Entity
import fr.uparis.chenliu.projet.data.dao.UsersDao
import fr.uparis.chenliu.projet.data.dao.SettingsDao
import fr.uparis.chenliu.projet.data.dao.QuizzDao

import fr.uparis.chenliu.projet.data.entity.AnswerEntity
import fr.uparis.chenliu.projet.data.entity.QuestionEntity
import fr.uparis.chenliu.projet.data.entity.SettingsEntity
import fr.uparis.chenliu.projet.data.entity.UsersEntity

@Database(entities = [UsersEntity::class, SettingsEntity::class, AnswerEntity::class, QuestionEntity::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usersDao(): UsersDao
    abstract fun settingsDao(): SettingsDao
    abstract fun quizzDao(): QuizzDao
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
