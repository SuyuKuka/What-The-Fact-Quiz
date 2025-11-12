package fr.uparis.chenliu.projet.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import fr.uparis.chenliu.projet.ui.theme.ProjetTheme
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel
import kotlinx.coroutines.delay

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(gameData: QuizzViewModel.GameData) {

    var isRunning = true

    if (gameData.gameEnded.value) {
        isRunning = false
    }

    TopAppBar(
        title = {
            Row {
                Text("Score : ${gameData.score.doubleValue} ")
                Text("Temps :" + chronometer(isRunning, gameData.time))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Red) // tmp
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyQuizzScreen(diff: String, subj: String, model: QuizzViewModel = viewModel()){
    // todo:simplifier tout ca

    val data = model.data

    val gameData = model.gameData

    val loading = model.loading

    model.init() // pour l'energie

    Scaffold(
        topBar = {
            MyTopBar(gameData)
        },
        containerColor = Color.LightGray // tmp
    ) { innerPadding ->
        if (loading.value) {
            //LoadingScreen()
            model.getQuestionAndAnswer(subj, diff)
        } else {
            Column(modifier = Modifier.padding(innerPadding)) {
                if (!gameData.gameEnded.value) {
                    Game(data)
                } else {
                    Result(model.translateFont(), gameData)
                }
            }
        }
    }
}

@Composable
fun Game(
    data: QuizzViewModel.Data,
    model: QuizzViewModel = viewModel()
){
    val i = remember { mutableIntStateOf(0) }

    when (data.types[i.intValue]) {
        0 -> Quizztype0(model.translateFont(), data, i, model)
        1 -> Quizztype1(model.translateFont(), data, i, model)
        2 -> Quizztype2(model.translateFont(), data, i, model)
        3 -> Quizztype3()
        4 -> Quizztype4(model.translateFont(), data, i, model)
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
                    onClick = { i.intValue = model.updateGame01(ans[0], i.intValue) },
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
    model: QuizzViewModel
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
            WriteField(
                value = text,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        text = newValue
                    }
                },
                label = "Nombre",
                modifier = Modifier,
                fontSize = 16.sp,
                onDone = {
                    val number = text.toIntOrNull()
                    if (number != null) {
                        i.intValue = model.updateGame01(text, i.intValue)
                    }
                },
                onlyNb = true
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)  // on prend ici
@Composable
fun DragImage(answer: String, url: String) {
    GlideImage(
        model = url,
        contentDescription = "Dragged Image",
        modifier = Modifier
            .fillMaxSize()
            .dragAndDropSource { _ ->
            DragAndDropTransferData(ClipData.newPlainText(answer, url))
        },
        loading = placeholder { Color.Red }
    )
}

@OptIn(ExperimentalGlideComposeApi::class) // on pose ici
@Composable
fun DropTargetImage(intValue: Int, font: List<Int>, model: QuizzViewModel, type: Int) {
    val currentImageAnswer = model.imageAnswer[intValue]
    val drag = if (currentImageAnswer.isNotEmpty()) {
        Modifier.dragAndDropSource { _ -> DragAndDropTransferData(ClipData.newPlainText("swap", intValue.toString())) }
    } else {
        Modifier
    }

    val dndTarget = remember(intValue) {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val draggedData = event.toAndroidDragEvent().clipData.getItemAt(0).text
                val answer = event.toAndroidDragEvent().clipData.description.label

                if (answer == "swap") {
                    val i = draggedData.toString().toInt()
                    model.swapAnswers(i, intValue)
                } else {
                    if (type == 2 || type == 3) {
                        model.replaceAnswer(answer.toString(), draggedData.toString(), intValue)
                    } else {
                        model.insertAnswer(answer.toString(), draggedData.toString(), intValue)
                    }
                }
                return true
            }
        }
    }
    Box(
        Modifier.border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
            .then(drag)
    ) {
        GlideImage(
            model = currentImageAnswer,
            contentDescription = "Dropped Image",
            modifier = Modifier
                .fillMaxSize()
                .dragAndDropTarget(
                    shouldStartDragAndDrop = { event ->
                        event.mimeTypes()
                            .contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                    },
                    target = dndTarget
                )
        )
        if (currentImageAnswer.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    DisplayText(list = font, text = (intValue+1).toString(), fontSize = 26.sp)
                }
            }
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
    val buildAnswer = model.buildAnswer

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(0.1f)
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
                Box (
                    modifier = Modifier
                        .aspectRatio(1.5f).padding(8.dp).fillMaxWidth()
                        .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
                ) {
                    DropTargetImage(data.answers[i.intValue].propositions.indexOf(ans), model.translateFont(), model, 2)
                }
            }
        }
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.Center,
        ){
            Button(onClick = { model.resetDragDrop() }, shape = RectangleShape) { DisplayText(list = font, text = "reset", textAlign = TextAlign.Center) }
            Spacer(modifier = Modifier.width(200.dp))
            Button(onClick = {
                i.intValue = model.updateGame2(buildAnswer, data.answers[i.intValue].answer.toList(), i.intValue)
                             }, shape = RectangleShape) { DisplayText(list = font, text = "valider", textAlign = TextAlign.Center) }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(data.answers[i.intValue].propositions) { ans ->
                Box (
                    modifier = Modifier
                        .aspectRatio(1.5f).padding(8.dp).fillMaxWidth()
                        .background(Color.Black)
                ) {
                    DragImage(answer = ans[0],url = ans[1])
                }
            }
        }
    }
}

@Composable
fun Quizztype3() {
    Text("33333333333333333333333333333333333333333")
}
@Composable
fun Quizztype4(
    font: List<Int>, data:
    QuizzViewModel.Data,
    i: MutableIntState,
    model: QuizzViewModel
) {
    val buildAnswer = model.buildAnswer
    val imageAnswer = model.imageAnswer
    val current = model.current
    val ans: List<String> = if (current.intValue < buildAnswer.size) {
        data.answers[i.intValue].propositions[current.intValue]
    } else {
        data.answers[i.intValue].propositions[current.intValue - 1]
    }

    if (current.intValue == 1) {
        buildAnswer[0] = data.answers[i.intValue].propositions[0][0]
        imageAnswer[0] = data.answers[i.intValue].propositions[0][1]
    }

    Column {
        // QUESTION HAUT
        Row(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(0.1f)
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DisplayText(list = font, text = data.question[i.intValue], textAlign = TextAlign.Center)
        }

        // EN HAUT
        LazyRow(
            modifier = Modifier.fillMaxWidth().weight(1f).padding(start = 16.dp).padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(count = current.intValue) { index ->
                if (index < buildAnswer.size) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (index == 0) {
                            InsertionDropTarget(index = 0, model = model)
                        }
                        Box(
                            modifier = Modifier
                                .width(250.dp)
                                .aspectRatio(1.5f)
                                .padding(8.dp)
                                .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
                        ) {
                            DropTargetImage(intValue = index, font = model.translateFont(), model = model, type = 4)
                        }
                        if (index != current.intValue) {
                            InsertionDropTarget(index = index + 1, model = model)
                        } else {
                            InsertionDropTarget(index = index, model = model)
                        }
                    }
                }
            }
        }

        // BOUTON MILIEU
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.Center,
        ){
            Button(onClick = { model.resetDragDrop() }, shape = RectangleShape) { DisplayText(list = font, text = "reset", textAlign = TextAlign.Center) }
            Spacer(modifier = Modifier.width(200.dp))
            Button(onClick = {
                i.intValue = model.updateGame4(buildAnswer, data.answers[i.intValue].answer.toList(), i.intValue)
            }, shape = RectangleShape) { DisplayText(list = font, text = "valider", textAlign = TextAlign.Center) }
        }

        // EN BAS
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .aspectRatio(1.5f).padding(8.dp).fillMaxWidth()
                .background(Color.Black)
        ) {
            if (current.intValue < buildAnswer.size) {
                DragImage(answer = ans[0], url = ans[1])
            } else {
                DisplayText(list = font, text = "C'est tout!", color = Color.White, fontSize = 32.sp,textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun InsertionDropTarget(index: Int, model: QuizzViewModel) {
    val dndTarget = remember(index) {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val draggedData = event.toAndroidDragEvent().clipData.getItemAt(0).text
                val answer = event.toAndroidDragEvent().clipData.description.label
                if (answer != "swap") {
                    model.insertAnswer(answer.toString(), draggedData.toString(), index)
                    return true
                }
                return false
            }
        }
    }

    Spacer(
        modifier = Modifier
            .width(20.dp)
            .height(200.dp)
            .dragAndDropTarget(
                shouldStartDragAndDrop = { event -> event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_PLAIN) },
                target = dndTarget
            )
    )
}

@Composable
fun chronometer(isRunning: Boolean, time: MutableDoubleState): String {
    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (true) {
                delay(1)
                time.doubleValue = time.doubleValue + 0.001
            }
        }
    }
    return "%.3f".format(time.doubleValue)
}

@Composable
fun Result(
    font: List<Int>,
    gameData: QuizzViewModel.GameData,
    model: QuizzViewModel = viewModel()
) {
    val activity = LocalActivity.current
    val alert = remember { mutableStateOf(false) }

    if (alert.value) {  // alerte le manque d'energie
        ShowAlert(font, alert, activity)
    }

    model.insertLeaderboard()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DisplayText(list = font, text = "RESULTAT", textAlign = TextAlign.Center)
        DisplayText(list = font, text = "Score : ${gameData.score.doubleValue}", textAlign = TextAlign.Center)

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