package fr.uparis.chenliu.projet.ui.quizz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.uparis.chenliu.projet.ui.menu.DisplayText
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarQuizz(model: QuizzViewModel, gameData: QuizzViewModel.GameData) {
    val font = model.currentFont
    val currentQuestion = model.currentQuestion

    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                QuestionNumber(model = model, font = font.value, currentQuestion = currentQuestion.intValue)
                ScoreAndTime(font = font.value, gameData = gameData)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Red)
    )
}

@Composable
fun QuestionNumber(model: QuizzViewModel, font: List<Int>, currentQuestion: Int) {
    if (!model.gameData.gameEnded.value) {
        DisplayText(
            text = "Question ${currentQuestion + 1}",
            font = font,
            fontSize = 24.sp,
            color = Color.Black
        )
    }
}

@Composable
fun ScoreAndTime(font: List<Int>, gameData: QuizzViewModel.GameData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DisplayText(text = "Score: ${gameData.score.intValue} ", font = font)
        DisplayText(text = "%.0fs".format(gameData.time.doubleValue), modifier = Modifier.padding(end = 16.dp), font = font)
    }
}