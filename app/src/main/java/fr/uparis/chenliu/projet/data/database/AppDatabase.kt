package fr.uparis.chenliu.projet.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.uparis.chenliu.projet.data.dao.DecoDao
import fr.uparis.chenliu.projet.data.dao.LeaderboardDao
import fr.uparis.chenliu.projet.data.dao.QuizzDao

// Import DAO et Entity
import fr.uparis.chenliu.projet.data.dao.UsersDao
import fr.uparis.chenliu.projet.data.dao.SettingsDao
import fr.uparis.chenliu.projet.data.dao.SubjectDao
import fr.uparis.chenliu.projet.data.entity.DecoEntity
import fr.uparis.chenliu.projet.data.entity.LeaderboardEntity
import fr.uparis.chenliu.projet.data.entity.QuizzEntity

import fr.uparis.chenliu.projet.data.entity.SettingsEntity
import fr.uparis.chenliu.projet.data.entity.SubjectEntity
import fr.uparis.chenliu.projet.data.entity.UsersEntity

@Database(entities = [
    UsersEntity::class,
    SettingsEntity::class,
    LeaderboardEntity::class,
    QuizzEntity::class,
    SubjectEntity::class,
    DecoEntity::class],
    version = 6
)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usersDao(): UsersDao
    abstract fun settingsDao(): SettingsDao
    abstract fun leaderboardDao(): LeaderboardDao
    abstract fun quizzDao(): QuizzDao
    abstract fun subjectDao(): SubjectDao
    abstract fun decoDao(): DecoDao
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
