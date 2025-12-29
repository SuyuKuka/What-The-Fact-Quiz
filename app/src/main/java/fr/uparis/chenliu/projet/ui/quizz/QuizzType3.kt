package fr.uparis.chenliu.projet.ui.quizz

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
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
import fr.uparis.chenliu.projet.ui.menu.DisplayText
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel

@Composable
fun Quizztype3(
    font: List<Int>, data:
    QuizzViewModel.Data,
    model: QuizzViewModel,
    modifier: Modifier
) {
    val buildAnswer = model.buildAnswer
    val currentQuestion = model.currentQuestion
    val currentImage = model.currentImage
    val ans: List<String> = if (currentImage.intValue < buildAnswer.size) {
        data.answers[currentQuestion.intValue].propositions[currentImage.intValue]
    } else {
        data.answers[currentQuestion.intValue].propositions[currentImage.intValue - 1]
    }

    LazyRow(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        items(count = currentImage.intValue) { index ->
            if (index < buildAnswer.size) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (index == 0) {
                        InsertionDropTarget(index = 0, model = model, type = 3)
                    }
                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .padding(8.dp)
                            .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
                    ) {
                        DropTargetImage(
                            intValue = index,
                            font = font,
                            model = model,
                            type = 3,
                            movable = buildAnswer[index] == ans[0]
                        )
                    }
                    if (index != currentImage.intValue) {
                        InsertionDropTarget(index = index + 1, model = model, type = 3)
                    } else {
                        InsertionDropTarget(index = index, model = model, type = 3)
                    }
                }
            }
        }
    }

    // EN BAS
    Box(
        contentAlignment = if ( currentImage.intValue < buildAnswer.size) {
            Alignment.BottomCenter
        } else {
            Alignment.Center
        },
        modifier = Modifier
            .aspectRatio(1.5f)
            .padding(8.dp)
            .height(200.dp)
            .background(Color.Black)
    ) {
        if (currentImage.intValue < buildAnswer.size) {
            DragImage(answer = ans[0], url = ans[1])
            DisplayText(list = font, text = ans[0], fontSize = 48.sp, color = Color.Red)
        } else {
            DisplayText(
                list = font,
                text = "C'est tout!",
                color = Color.White,
                fontSize = 32.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}