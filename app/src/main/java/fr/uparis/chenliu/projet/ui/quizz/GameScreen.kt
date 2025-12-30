package fr.uparis.chenliu.projet.ui.quizz

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uparis.chenliu.projet.ui.menu.DisplayBackground
import fr.uparis.chenliu.projet.ui.menu.DisplayText
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel

@Composable
fun GameScreen(
    data: QuizzViewModel.Data,
    model: QuizzViewModel = viewModel()
){
    val currentQuestion = model.currentQuestion
    val background = model.currentBackground
    val font = model.currentFont
    val typeQuestion = data.types[currentQuestion.intValue]
    val height = if (typeQuestion == 0 || typeQuestion == 1) 0.5f else 0.20f
    Box(modifier = Modifier.fillMaxSize()) {
        DisplayBackground(currentBackground = background.intValue)
        GameContent(
            model = model,
            font = font.value,
            height = height,
            data = data,
            typeQuestion = typeQuestion,
            currentQuestion = currentQuestion.intValue
        )
    }
}

@Composable
fun GameContent(model: QuizzViewModel, font: List<Int>, height: Float, data: QuizzViewModel.Data, typeQuestion: Int, currentQuestion: Int) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .fillMaxHeight(height)
                .align(Alignment.CenterHorizontally),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DisplayText(
                text = data.question[currentQuestion],
                font = font,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }
        QuizzType(model = model, font = font, data = data, typeQuestion = typeQuestion, modifier = Modifier.weight(1f))
    }
}

@Composable
fun QuizzType(model: QuizzViewModel, font: List<Int>, data: QuizzViewModel.Data, typeQuestion: Int, modifier: Modifier) {
    when (typeQuestion) {
        0 -> Quizztype0(
            font = font,
            data = data,
            model = model,
            modifier = modifier
        )
        1 -> Quizztype1(
            font = font,
            model = model,
            data = data
        )
        2 -> Quizztype2(
            font = font,
            data = data,
            model = model,
            modifier = modifier
        )
        3 -> Quizztype3(
            font = font,
            data = data,
            model = model,
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
                .padding(end = 16.dp)
        )
        4 -> Quizztype4(
            font = font,
            data = data,
            model = model,
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
                .padding(end = 16.dp)
        )
    }
}

@Composable
fun AnswerSlot(model: QuizzViewModel, font: List<Int>, currentImage: Int, modifier: Modifier = Modifier, quizzType: Int, buildAnswer: List<String>, answer: List<String>) {
    LazyRow(
        modifier = modifier
    ) {
        items(count = currentImage) { index ->
            Row {
                if (index == 0) {
                    InsertionDropTarget(model = model, indexAnswer = 0, quizzType = 4)
                }
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .padding(8.dp)
                        .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
                ) {
                    DropTargetImage(
                        model = model,
                        font = font,
                        indexAnswer = index,
                        quizzType = quizzType,
                        movable = if (quizzType == 3) {
                            buildAnswer[index] == answer[0]
                        } else {
                            false
                        }
                    )
                }
                if (index != currentImage) {
                    InsertionDropTarget(model = model, indexAnswer = index + 1, quizzType = 4)
                } else {
                    InsertionDropTarget(model = model, indexAnswer = index, quizzType = 4)
                }
            }
        }
    }
}

@Composable
fun PropositionsSlot(font: List<Int>, answer: List<String>, buildAnswer: List<String>, currentImage: Int, modifier: Modifier) {
    Box(
        contentAlignment = if ( currentImage < buildAnswer.size) {
            Alignment.BottomCenter
        } else {
            Alignment.Center
        },
        modifier = modifier
    ) {
        if (currentImage < buildAnswer.size) {
            DragImage(answer = answer[0], image = answer[1])
            DisplayText(text = answer[0], font = font, fontSize = 48.sp, color = Color.Red)
        } else {
            DisplayText(
                text = "C'est tout!",
                font = font,
                color = Color.White,
                fontSize = 32.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}