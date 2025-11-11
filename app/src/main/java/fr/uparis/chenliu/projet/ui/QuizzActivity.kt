package fr.uparis.chenliu.projet.ui

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
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

    val data = QuizzViewModel.Data(
        question = model.question,
        answers =  model.answers,
        types = model.types
    )

    val gameData = QuizzViewModel.GameData(
        answerHistory = model.answerHistory,
        goodAnswer = model.goodAnswer,
        questionHistory = model.questionHistory,
        nbQuestion = model.nbQuestion
    )

    val score = model.score

    val loading = model.loading.value

    model.init() // pour l'energie

    if (loading) {
        model.getQuestionAndAnswer(subj, diff)
    } else {

        Column(modifier = Modifier.padding(top = 24.dp)) {
            if (gameData.nbQuestion.intValue > 0) {
                Game(data, model)
            } else {
                Result(model.translateFont(), score, gameData)
            }
        }
    }
}

@Composable
fun Game(
    data: QuizzViewModel.Data,
    model: QuizzViewModel
){
    println(data.question.toString())
    println(data.types.toString())
    val i = remember { mutableIntStateOf(0) }

    when (data.types[i.intValue]) {
        0 -> Quizztype0(model.translateFont(), data, i, model)
        1 -> Quizztype1(model.translateFont(), data, i, model)
        2 -> Quizztype2(model.translateFont(), data, i, model)
        3 -> Quizztype3()
        4 -> Quizztype4()
    }
}

@Composable
fun Quizztype0 (
    font: List<Int>,
    data: QuizzViewModel.Data,
    i: MutableIntState,
    model: QuizzViewModel
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(0.5f)
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DisplayText(list = font, text = data.question[i.intValue], textAlign = TextAlign.Center)
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(data.answers[i.intValue].propositions) { ans ->
                Button(
                    onClick = { i.intValue = model.updateGame(ans, i.intValue) },
                    modifier = Modifier.aspectRatio(1.5f).padding(8.dp).fillMaxWidth(),
                    shape = RectangleShape
                ) {
                    DisplayText(list = font, text = ans[0], textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun Quizztype1 (
    font: List<Int>,
    data: QuizzViewModel.Data,
    i: MutableIntState,
    model: QuizzViewModel = viewModel()
) {
    var text by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.5f)
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DisplayText(list = font, text = data.question[i.intValue], textAlign = TextAlign.Center)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.5f)
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        text = newValue
                    }
                },
                label = { Text("Nombre") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        val number = text.toIntOrNull()
                        if (number != null) {
                            i.intValue = model.updateGame(listOf(text), i.intValue)
                        }
                    }
                )
            )

        }
    }
}

@Composable
fun Quizztype2(
    font: List<Int>,
   data: QuizzViewModel.Data,
   i: MutableIntState,
   model: QuizzViewModel
) {
    val order = remember { mutableStateListOf<String>() }


    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(0.5f)
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DisplayText(list = font, text = data.question[i.intValue], textAlign = TextAlign.Center)
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(data.answers[i.intValue].propositions) { ans ->
                Box(contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = ans[1],
                        contentDescription = ans[0],
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
        Button(
            onClick = { i.intValue = model.updateGame(order, i.intValue) },
            shape = RectangleShape
        ) {
            DisplayText(list = font, text = "Valider")
        }
    }
}
@Composable
fun Quizztype3() {
    Text("333333333333333333333333333333333333333")
}
@Composable
fun Quizztype4() {
    Text("4444444444444444444444444444444444444")
}

@Composable
fun Result(
    font: List<Int>,
    score: MutableDoubleState,
    gameData: QuizzViewModel.GameData,
    model: QuizzViewModel = viewModel()
) {
    val activity = LocalActivity.current
    val alert = remember { mutableStateOf(false) }

    if (alert.value) {  // alerte le manque d'energie
        ShowAlert(font, alert, activity)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DisplayText(list = font, text = "RESULTAT", textAlign = TextAlign.Center)
        DisplayText(list = font, text = "Score : ${score.doubleValue}", textAlign = TextAlign.Center)

        LazyColumn(modifier = Modifier.fillMaxWidth().weight(4f)) {

            itemsIndexed(gameData.questionHistory) { i, question ->
                if (gameData.answerHistory[i] == gameData.goodAnswer[i]) {
                    DisplayText(list = font,"Question ${i+1}: $question", color = Color.Black)
                    DisplayText(list = font,"Réponse -> ${gameData.goodAnswer[i]}\n", color = Color.Green)
                } else {
                    DisplayText(list = font,"Question ${i+1}: $question", color = Color.Black)
                    DisplayText(list = font,"Faux : ${gameData.answerHistory[i]}", color = Color.Red)
                    DisplayText(list = font,"Réponse -> ${gameData.goodAnswer[i]}\n", color = Color.Blue)
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().weight(2f)){
            Button(
                onClick = { activity?.finish() },
                modifier = Modifier.padding(8.dp)
            ) {
                DisplayText(list = font, text = "Accueil")
            }
            Button(
                onClick = {
                    if (model.energy.intValue == 0) {
                        alert.value = true
                        return@Button
                    }
                    model.resetStats(gameData)
                },
                modifier = Modifier.padding(8.dp)
            ) {
                DisplayText(list = font, text = "Rejouer")
            }
        }
    }
}

@Composable
fun ShowAlert(font: List<Int>, alert: MutableState<Boolean>, activity: Activity?) {
    Alert(
        list = font,
        onConfirm = { alert.value = false ; activity?.finish() },
        onCancel = { alert.value = false },
        title = "Joue Sans Limite ! 👑",
        text =
            """
Tu n'as plus d'énergie ! 😱
Ne perds plus ton temps à attendre !
Achète l'abonnement prenium pour bénéficier de l'énergie A L'INFINI et jouer sans interruption
            """,
        onConfirmText = "Continuer",
        onCancelText = "Fermer"
    )
}