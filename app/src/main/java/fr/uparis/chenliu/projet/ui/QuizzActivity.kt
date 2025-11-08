package fr.uparis.chenliu.projet.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uparis.chenliu.projet.ui.theme.ProjetTheme
import fr.uparis.chenliu.projet.ui.viewmodel.MainViewModel
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel

class QuizzActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProjetTheme {

                val difficulty = intent.getStringExtra("difficulty")!!
                MyQuizzScreen(difficulty)
            }
        }
    }
}

@Composable
fun MyQuizzScreen(diff: String, model: QuizzViewModel = viewModel()){

    val question = model.question.value
    model.getQuestionAndAnswer(1,diff)
    val answers = model.answers
    val bad = Toast.makeText(LocalContext.current, "NAAAAAAH", Toast.LENGTH_SHORT)
    val good = Toast.makeText(LocalContext.current, "NICE JOB", Toast.LENGTH_SHORT)

    Column (modifier = Modifier.padding(top =24.dp)) {
        Column {
            Row {
                Text(text = question)
            }
            Row {
                Button(onClick = { if (answers[0].value.validity) { good.show() } else { bad.show()} }) { Text(text = answers[0].value.answer) }
                Button(onClick = { if (answers[1].value.validity) { good.show() } else { bad.show()} }) { Text(text = answers[1].value.answer) }
            }
            Row {
                Button(onClick = { if (answers[2].value.validity) { good.show() } else { bad.show()} }) { Text(text = answers[2].value.answer) }
                Button(onClick = { if (answers[3].value.validity) { good.show() } else { bad.show()} }) { Text(text = answers[3].value.answer) }
            }
        }
    }
}
