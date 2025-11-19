package fr.uparis.chenliu.projet.ui.quizz

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import fr.uparis.chenliu.projet.R
import fr.uparis.chenliu.projet.ui.menu.DisplayText
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyQuizzScreen(diff: String, subj: String, model: QuizzViewModel = viewModel()){
    val data = model.data
    val gameData = model.gameData
    val loading = model.loading

    model.init() // pour l'energie

    Scaffold(
        topBar = {
            TopBarQuizz(gameData, model)
        },
        containerColor = Color.LightGray // tmp
    ) { innerPadding ->
        if (loading.value) {
            model.getQuestionAndAnswer(subj, diff)
        } else {
            Column(modifier = Modifier.padding(innerPadding)) {
                if (!gameData.gameEnded.value) {
                    GameScreen(data)
                } else {
                    ResultScreen(model.translateFont(), gameData)
                }
            }
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
        loading = placeholder { R.drawable.placeholder }
    )
}

@OptIn(ExperimentalGlideComposeApi::class) // on pose ici
@Composable
fun DropTargetImage(intValue: Int, font: List<Int>, model: QuizzViewModel, type: Int, movable: Boolean = true) {
    val currentImageAnswer = model.imageAnswer[intValue]
    val drag = if ((currentImageAnswer.isNotEmpty() && type != 3) || (type == 3 && movable)) {
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
                    if (type == 2) {
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
                    DisplayText(list = font, text = (intValue + 1).toString(), fontSize = 26.sp)
                }
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