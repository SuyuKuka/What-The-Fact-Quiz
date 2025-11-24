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
    val textFieldQuestion = model.textFieldQuestion
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WriteField(
            value = textFieldQuestion.value,
            onValueChange = {
                newValue -> textFieldQuestion.value = newValue
            },
            label = "Réponse",
            modifier = Modifier,
            fontSize = 16.sp,
            onDone = {
                val number = textFieldQuestion.value.toIntOrNull()
                if (number != null) {
                    model.updateGame(
                        answerList = listOf(textFieldQuestion.value),
                        verify = data.answers[currentQuestion.intValue].answer,
                        type = 1
                    )
                }
            }
        )
    }
}