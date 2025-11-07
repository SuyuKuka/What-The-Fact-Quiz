package fr.uparis.chenliu.projet.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Import DAO et Entity
import fr.uparis.chenliu.projet.data.dao.UsersDao
import fr.uparis.chenliu.projet.data.entity.UsersEntity

import fr.uparis.chenliu.projet.data.dao.SettingsDao
import fr.uparis.chenliu.projet.data.entity.SettingsEntity
import java.io.File

@Database(entities = [UsersEntity::class, SettingsEntity::class], version = 2)
abstract class QuizzDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var dbInstance: QuizzDatabase? = null
        fun getInstance(c: Context): QuizzDatabase {
            if (dbInstance == null) {
                dbInstance = Room.databaseBuilder(c.applicationContext, QuizzDatabase::class.java, "quizz.db")
                    .createFromFile(File("mypath"))
                    .fallbackToDestructiveMigration(true).build()
            }
            return dbInstance!!
        }
    }
}
