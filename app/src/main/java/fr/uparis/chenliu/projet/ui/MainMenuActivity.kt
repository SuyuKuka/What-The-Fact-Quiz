package fr.uparis.chenliu.projet.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uparis.chenliu.projet.ui.theme.ProjetTheme
import fr.uparis.chenliu.projet.ui.viewmodel.MainViewModel

class MainMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjetTheme {
                val username = intent.getStringExtra("username")!!
                if (username == "") {
                    finish()
                }
                MyScreenMainMenu(username)
            }
        }
    }
}

@Composable
fun MyScreenMainMenu(username: String, model: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val intent = Intent(context, QuizzActivity::class.java)
    Column {
        Text("Hello $username!")
        Button(onClick = { context.startActivity(intent) } ) { Text("QUIZZ (temporaire)") }
        Button(onClick = { model.deleteAllUsers() }) { Text("Supprimer les données") }
    }

}