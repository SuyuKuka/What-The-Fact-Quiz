package fr.uparis.chenliu.projet.ui

import android.R.attr.onClick
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
import fr.uparis.chenliu.projet.ui.ui.theme.ProjetTheme
import fr.uparis.chenliu.projet.ui.viewmodel.MainViewModel
import java.util.Map.entry

class QuizzActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProjetTheme {
                MyQuizzScreen()
            }
        }
    }
}

@Composable
fun MyQuizzScreen(model: MainViewModel = viewModel()){


    model.getQuestionAndAnswer(1,1) // todo choisir selon la diff, et la matiere
    val question = model.question.value
    val answers = model.answers
    val answersValidity = model.answersValidity


    val bad = Toast.makeText(LocalContext.current, "NAAAAAAH", Toast.LENGTH_SHORT)
    val good = Toast.makeText(LocalContext.current, "NICE JOB", Toast.LENGTH_SHORT)



    Column (modifier = Modifier.padding(top =24.dp)) {
        Column {
            Row {
                Text(text = question)
            }
            Row {   // todo: ordre aléatoire
                Button(onClick = { if (answersValidity[0].value) { good.show() } else { bad.show()} }) { Text(text = answers[0].value) }
                Button(onClick = { if (answersValidity[1].value) { good.show() } else { bad.show()} }) { Text(text = answers[1].value) }
            }
            Row {
                Button(onClick = { if (answersValidity[2].value) { good.show() } else { bad.show()} }) { Text(text = answers[2].value) }
                Button(onClick = { if (answersValidity[3].value) { good.show() } else { bad.show()} }) { Text(text = answers[3].value) }
            }
        }
    }
}
