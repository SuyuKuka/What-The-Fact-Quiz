package fr.uparis.chenliu.projet.ui

import android.os.Bundle
import android.util.Log.i
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uparis.chenliu.projet.ui.theme.ProjetTheme
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel

class QuizzActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProjetTheme {

                val difficulty = intent.getStringExtra("difficulty")!!
                val subject = intent.getStringExtra("subject")!!
                MyQuizzScreen(difficulty, subject)
            }
        }
    }
}

@Composable
fun MyQuizzScreen(diff: String, subj: String, model: QuizzViewModel = viewModel()){
    // todo:simplifier tout ca
    val answerHistory = remember { mutableStateListOf<QuizzViewModel.AnswerSummary>() }
    val goodAnswer = remember { mutableStateListOf<String>() }
    val questionHistory = remember { mutableStateListOf<String>() }
    val score = remember { mutableIntStateOf(0) }
    val nbQuestion = remember { mutableIntStateOf(10) }

    val question = model.question.value
    val answers = model.answers
    LaunchedEffect(subj, diff,nbQuestion.intValue) {
        model.getQuestionAndAnswer(subj, diff)
    }
    Column(modifier = Modifier.padding(top = 24.dp)) {
        if (nbQuestion.intValue > 0) {
            Game(question, answers, score, nbQuestion, answerHistory,questionHistory,goodAnswer)
        } else {
            Result(score, nbQuestion, answerHistory,questionHistory,goodAnswer)
        }
    }
}

@Composable
fun Game(
    question: String,
    answers: Array<MutableState<QuizzViewModel.AnswerSummary>>,
    score: MutableIntState,
    nbQuestion: MutableIntState,
    answerHistory: SnapshotStateList<QuizzViewModel.AnswerSummary>,
    questionHistory: SnapshotStateList<String>,
    goodAnswer: SnapshotStateList<String>
){

    val bad = Toast.makeText(LocalContext.current, "NAAAAAAH", Toast.LENGTH_SHORT)
    val good = Toast.makeText(LocalContext.current, "NICE JOB", Toast.LENGTH_SHORT)

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(0.5f)
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question,
                textAlign = TextAlign.Center
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier
                .weight(1f)
        ) {
            items(answers) { ans ->
                if (ans.value.validity) {
                    goodAnswer.add(ans.value.answer)
                }
                Button(
                    onClick = {
                        answerHistory.add(ans.value)
                        questionHistory.add(question)
                        if (ans.value.validity) {
                            good.show(); ++score.intValue; nbQuestion.intValue--
                        } else {
                            bad.show(); nbQuestion.intValue--
                        } },
                    modifier = Modifier
                        .aspectRatio(1.5f)
                        .padding(8.dp)
                        .fillMaxWidth(),
                    shape = RectangleShape
                ) {
                    Text(text = ans.value.answer)
                }
            }
        }
    }
}

@Composable
fun Result(
    score: MutableIntState,
    nbQuestion: MutableIntState,
    answerHistory: SnapshotStateList<QuizzViewModel.AnswerSummary>,
    questionHistory: SnapshotStateList<String>,
    goodAnswer: SnapshotStateList<String>
) {
    val a = LocalActivity.current

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("RESULTAT", textAlign = TextAlign.Center)
        Text("Score : ${score.intValue}", textAlign = TextAlign.Center)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(4f)
        ) {
            itemsIndexed(questionHistory) { i, question ->

            val index = i+1
            if (answerHistory[i].validity) {
                    Text("Question ${index}: $question", color = Color.Green)
                    Text("Réponse : ${answerHistory[i].answer}", color = Color.Green)
                }
                else {
                    Text("Question ${index}: $question", color = Color.Red)
                    Text("Faux : ${answerHistory[i].answer}", color = Color.Red)
                    Text("Réponse -> ${goodAnswer[i]}", color = Color.Blue)
                }

            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
        ){
            Button(
                onClick = { a?.finish() },
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(text = "Maison")
            }
            Button(
                onClick = { nbQuestion.intValue = 10; score.intValue = 0; answerHistory.clear(); questionHistory.clear()},
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(text = "Rejouer")
            }
        }

    }
}

