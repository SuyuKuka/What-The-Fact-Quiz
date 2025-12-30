package fr.uparis.chenliu.projet.ui.quizz

import android.content.ClipData
import android.content.ClipDescription
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import fr.uparis.chenliu.projet.R
import fr.uparis.chenliu.projet.ui.HideSystemBar
import fr.uparis.chenliu.projet.ui.menu.DisplayText
import fr.uparis.chenliu.projet.ui.theme.ProjetTheme
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel

class QuizzActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjetTheme {
                val subject = intent.getStringExtra("subject")!!
                val difficulty = intent.getStringExtra("difficulty")!!
                HideSystemBar(window)
                MyQuizzScreen(subject = subject, difficulty = difficulty)
            }
        }
    }
}

@Composable
fun MyQuizzScreen(subject: String, difficulty: String, model: QuizzViewModel = viewModel()){
    val data = model.data
    val gameData = model.gameData
    val loading = model.loading
    val context = LocalContext.current

    model.init(context = context)

    Scaffold(
        topBar = { TopBarQuizz(model = model, gameData = gameData) },
        bottomBar = { BottomBarQuizz(model = model, gameData = gameData) }
    ) { innerPadding ->
        if (loading.value) {
            model.getQuestionAndAnswer(subject = subject, difficulty = difficulty)
        } else {
            Column(modifier = Modifier.padding(innerPadding)) {
                if (!gameData.gameEnded.value) {
                    GameScreen(data = data.value)
                } else {
                    ResultScreen(
                        model = model,
                        font = model.currentFont.value,
                        background = model.currentBackground.intValue,
                        gameData = gameData,
                        subject = subject
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DragImage(answer: String, image: String) {
    GlideImage(
        model = image,
        contentDescription = "Dragged Image",
        modifier = Modifier
            .fillMaxSize()
            .dragAndDropSource { _ ->
            DragAndDropTransferData(ClipData.newPlainText(answer, image))
        },
        loading = placeholder { R.drawable.placeholder },
        contentScale = ContentScale.Crop
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DropTargetImage(model: QuizzViewModel, font: List<Int>, indexAnswer: Int, quizzType: Int, movable: Boolean = true) {
    val currentImageAnswer = model.imageAnswer[indexAnswer]
    val drag = if ((currentImageAnswer.isNotEmpty() && quizzType == 2) || (quizzType == 3 && movable)) {
        Modifier.dragAndDropSource { _ -> DragAndDropTransferData(ClipData.newPlainText("swap", indexAnswer.toString())) }
    } else {
        Modifier
    }
    val dragTarget = remember(indexAnswer) {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val answer = event.toAndroidDragEvent().clipData.description.label
                val image = event.toAndroidDragEvent().clipData.getItemAt(0).text

                if (answer == "swap") {
                    val indexImage = image.toString().toInt()
                    model.swapAnswers(indexAnswer = indexAnswer, indexImage = indexImage)
                } else {
                    if (quizzType == 2) {
                        model.replaceAnswer(
                            answer = answer.toString(),
                            image = image.toString(),
                            indexAnswer = indexAnswer
                        )
                    } else {
                        model.insertAnswer(
                            answer = answer.toString(),
                            image = image.toString(),
                            indexAnswer = indexAnswer,
                            quizzType = quizzType
                        )
                    }
                }
                return true
            }
        }
    }

    Box(
        Modifier.border(2.dp, Color.Gray)
    ) {
        SlotDrop(font = font, indexAnswer = indexAnswer, dragTarget = dragTarget, currentImageAnswer = currentImageAnswer)
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SlotDrop(font: List<Int>, indexAnswer: Int, dragTarget: DragAndDropTarget, currentImageAnswer: String) {
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
                target = dragTarget
            ),
        contentScale = ContentScale.Crop
    )
    if (currentImageAnswer.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DisplayText(text = (indexAnswer + 1).toString(), font = font, fontSize = 26.sp)
        }
    }
}

@Composable
fun InsertionDropTarget(model: QuizzViewModel, indexAnswer: Int, quizzType: Int) {
    val dragTarget = remember(quizzType) {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val answer = event.toAndroidDragEvent().clipData.description.label
                val image = event.toAndroidDragEvent().clipData.getItemAt(0).text
                if (answer != "swap") {
                    model.insertAnswer(answer = answer.toString(), image = image.toString(), indexAnswer = indexAnswer, quizzType = quizzType)
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
                target = dragTarget
            )
    )
}