package fr.uparis.chenliu.projet.ui.quizz

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.uparis.chenliu.projet.ui.menu.DisplayText
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel

@Composable
fun Quizztype0 (
    font: List<Int>,
    data: QuizzViewModel.Data,
    model: QuizzViewModel,
    modifier: Modifier
) {
    val currentQuestion = model.currentQuestion
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
    ) {
        items(data.answers[currentQuestion.intValue].propositions) { answer ->
            Button(
                onClick = { model.updateGame(
                    answerList = listOf(answer[0]),
                    goodAnswers = data.answers[currentQuestion.intValue].answer,
                    type = 0
                )},
                modifier = Modifier.aspectRatio(1.5f).padding(8.dp).fillMaxWidth(),
                shape = RectangleShape
            ) {
                if (answer.isNotEmpty()) {
                    DisplayText(text = answer[0], font = font, textAlign = TextAlign.Center)
                }
            }
        }
    }
}