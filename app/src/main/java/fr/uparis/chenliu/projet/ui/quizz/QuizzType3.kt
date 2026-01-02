package fr.uparis.chenliu.projet.ui.quizz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    val answer: List<String> = if (currentImage.intValue < buildAnswer.size) {
        data.answers[currentQuestion.intValue].propositions[currentImage.intValue]
    } else {
        data.answers[currentQuestion.intValue].propositions[currentImage.intValue - 1]
    }

    AnswerSlot(
        model = model,
        font = font,
        currentImage = currentImage.intValue,
        modifier = modifier,
        quizzType = 3,
        buildAnswer = buildAnswer,
        answer = answer
    )
    PropositionsSlot(
        font = font,
        buildAnswer = buildAnswer,
        answer = answer,
        currentImage = currentImage.intValue,
        modifier = Modifier.aspectRatio(1.5f).padding(8.dp).height(200.dp).background(Color.Black)
    )
}