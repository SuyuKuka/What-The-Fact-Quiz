package fr.uparis.chenliu.projet.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.uparis.chenliu.projet.R
import fr.uparis.chenliu.projet.data.database.AppDatabase
import fr.uparis.chenliu.projet.data.entity.SettingsEntity
import fr.uparis.chenliu.projet.data.entity.UsersEntity
import kotlinx.coroutines.launch

class MainMenuViewModel(application: Application): AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private var user = mutableStateOf<UsersEntity?>(null)
    private var settings = mutableStateOf<SettingsEntity?>(null)

    val username = mutableStateOf("")
    val music = mutableFloatStateOf(0f)
    val sound = mutableFloatStateOf(0f)
    val background = mutableIntStateOf(0)
    val font = mutableIntStateOf(0)
    val preferredTopics = mutableStateOf("")

    val backgroundList = listOf("Fond 1", "Fond 2", "Fond 3")
    val fontList = listOf("Police 1", "Police 2", "Police 3")
    val saved = mutableStateOf(true)

    fun init(username: String) {
        this.username.value = username
        viewModelScope.launch {
            user.value = db.usersDao().getUser()
            settings.value = db.settingsDao().getSetting()

            music.floatValue = settings.value!!.music
            sound.floatValue = settings.value!!.sound
            background.intValue = settings.value!!.background
            font.intValue = settings.value!!.font
            preferredTopics.value = settings.value!!.preferredTopics
        }
    }

    fun updateSettings() {
        viewModelScope.launch {
            db.settingsDao().updateSettings(SettingsEntity(user.value!!.userId, sound.floatValue, music.floatValue, background.intValue, font.intValue, preferredTopics.value))
        }
    }

    fun translateBackground(): Int {
        val background = when(background.intValue) {
            0 -> R.drawable.b0
            1 -> R.drawable.b1
            2 -> R.drawable.b2
            else -> {
                R.drawable.b0
            }
        }
        return background
    }

    fun undoSettings() {
        music.floatValue = settings.value!!.music
        sound.floatValue = settings.value!!.sound
        background.intValue = settings.value!!.background
        font.intValue = settings.value!!.font
        preferredTopics.value = settings.value!!.preferredTopics
    }
}