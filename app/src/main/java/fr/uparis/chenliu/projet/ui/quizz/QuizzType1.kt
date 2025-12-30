package fr.uparis.chenliu.projet.ui.quizz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import fr.uparis.chenliu.projet.ui.WriteField
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel

@Composable
fun Quizztype1 (
    font: List<Int>,
    data: QuizzViewModel.Data,
    model: QuizzViewModel
) {
    val currentQuestion = model.currentQuestion
    val textFieldAnswer = model.textFieldAnswer
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
        horizontalArrangement = Arrangement.Center,

    ) {
        WriteField(
            value = textFieldAnswer.value,
            onValueChange = {
                    newValue -> textFieldAnswer.value = newValue
            },
            label = "",
            modifier = Modifier,
            font = font,
            fontSize = 16.sp,
            onDone = {
                model.updateGame(
                    answerList = listOf(textFieldAnswer.value),
                    goodAnswers = data.answers[currentQuestion.intValue].answer,
                    type = 1
                )
            }
        )
    }
}