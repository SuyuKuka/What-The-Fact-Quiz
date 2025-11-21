package fr.uparis.chenliu.projet.ui.quizz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import fr.uparis.chenliu.projet.ui.WriteField
import fr.uparis.chenliu.projet.ui.menu.DisplayText
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel

@Composable
fun Quizztype1 (
    font: List<Int>,
    data: QuizzViewModel.Data,
    model: QuizzViewModel
) {
    val currentQuestion = model.currentQuestion
    var text by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f),
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
            font = font,
            onDone = {
                val number = text.toIntOrNull()
                if (number != null) {
                    model.updateGame(
                        answerList = listOf(text),
                        verify = data.answers[currentQuestion.intValue].answer,
                        type = 1
                    )
                }
            },
            onlyNb = true
        )
        Button(onClick = {
            model.updateGame(
                answerList = listOf(text),
                verify = data.answers[currentQuestion.intValue].answer,
                type = 1
            )
        }) { DisplayText(list = font, text = "Valider") }
    }
}