package fr.uparis.chenliu.projet.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uparis.chenliu.projet.ui.theme.ProjetTheme
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel

class QuizzActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    val answerHistory = remember { mutableStateListOf<String>() }
    val goodAnswer = remember { mutableStateListOf<String>() }
    val questionHistory = remember { mutableStateListOf<String>() }
    val score = remember { mutableIntStateOf(0) }
    val nbQuestion = remember { mutableIntStateOf(10) }

    val question = model.question
    val answers = model.answers
    val loading = model.loading.value

    model.init()

    if (loading) {
        model.getQuestionAndAnswer(subj, diff)
    } else {
        Column(modifier = Modifier.padding(top = 24.dp)) {
            if (nbQuestion.intValue > 0) {
                Game(
                    font = model.translateFont(),
                    question = question,
                    answers = answers,
                    score = score,
                    nbQuestion = nbQuestion,
                    answerHistory = answerHistory,
                    questionHistory = questionHistory,goodAnswer
                )
            } else {
                Result(model = model,
                    font = model.translateFont(),
                    score = score,
                    nbQuestion = nbQuestion,
                    answerHistory = answerHistory,
                    questionHistory = questionHistory,
                    goodAnswer = goodAnswer
                )
            }
        }
    }
}

@Composable
fun Game(
    font: List<Int>,
    question: MutableList<String>,
    answers: MutableList<QuizzViewModel.AnswerSummary>,
    score: MutableIntState,
    nbQuestion: MutableIntState,
    answerHistory: SnapshotStateList<String>,
    questionHistory: SnapshotStateList<String>,
    goodAnswer: SnapshotStateList<String>,
){
    val i = remember { mutableIntStateOf(0) }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(0.5f)
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DisplayText(list = font, text = question[i.intValue], textAlign = TextAlign.Center)
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier
                .weight(1f)
        ) {
            items(answers[i.intValue].propositions) { ans ->
                Button(
                    onClick = {
                        answerHistory.add(ans)
                        questionHistory.add(question[i.intValue])
                        goodAnswer.add(answers[i.intValue].answer)
                        if (ans == answers[i.intValue].answer) { ++score.intValue }
                        nbQuestion.intValue--
                        if (i.intValue == 9) {
                            i.intValue == 0
                        } else {
                            i.intValue++
                        }
                    },
                    modifier = Modifier
                        .aspectRatio(1.5f)
                        .padding(8.dp)
                        .fillMaxWidth(),
                    shape = RectangleShape
                ) {
                    DisplayText(list = font, text = ans, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun Result(
    model: QuizzViewModel,
    font: List<Int>,
    score: MutableIntState,
    nbQuestion: MutableIntState,
    answerHistory: SnapshotStateList<String>,
    questionHistory: SnapshotStateList<String>,
    goodAnswer: SnapshotStateList<String>,
) {
    val activity = LocalActivity.current
    val alert = remember { mutableStateOf(false) }

    if (alert.value) {
        Alert(
            list = font,
            onConfirm = {
                alert.value = false
                activity?.finish()
            },
            onCancel = {
                alert.value = false
            },
            title = "Joue Sans Limite ! 👑",
            text = "Tu n'as plus d'énergie ! 😱\nNe perds plus ton temps à attendre !\nAchète l'abonnement prenium pour bénéficier de l'énergie A L'INFINI et jouer sans interruption",
            onConfirmText = "Continuer",
            onCancelText = "Fermer"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DisplayText(list = font, text = "RESULTAT", textAlign = TextAlign.Center)
        DisplayText(list = font, text = "Score : ${score.intValue}", textAlign = TextAlign.Center)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(4f)
        ) {
            itemsIndexed(questionHistory) { i, question ->
                if (answerHistory[i] == goodAnswer[i]) {
                    DisplayText(list = font,"Question ${i+1}: $question", color = Color.Black)
                    DisplayText(list = font,"Réponse -> ${goodAnswer[i]}\n", color = Color.Green)
                } else {
                    DisplayText(list = font,"Question ${i+1}: $question", color = Color.Black)
                    DisplayText(list = font,"Faux : ${answerHistory[i]}", color = Color.Red)
                    DisplayText(list = font,"Réponse -> ${goodAnswer[i]}\n", color = Color.Blue)
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
        ){
            Button(
                onClick = { activity?.finish() },
                modifier = Modifier
                    .padding(8.dp)
            ) {
                DisplayText(list = font, text = "Accueil")
            }
            Button(
                onClick = {
                    if (model.energy.intValue == 0) {
                        alert.value = true
                        return@Button
                    }
                    model.removeEnergy()
                    nbQuestion.intValue = 10
                    score.intValue = 0
                    answerHistory.clear()
                    questionHistory.clear()
                    goodAnswer.clear()
                    model.loading.value = true
                },
                modifier = Modifier
                    .padding(8.dp)
            ) {
                DisplayText(list = font, text = "Rejouer")
            }
        }

    }
}

