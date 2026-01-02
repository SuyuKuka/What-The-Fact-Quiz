package fr.uparis.chenliu.projet.ui.quizz

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.uparis.chenliu.projet.ui.menu.DisplayText
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBarQuizz(model: QuizzViewModel, gameData: QuizzViewModel.GameData) {
    val activity = LocalActivity.current
    val alert = remember { mutableStateOf(false) }
    val font = model.currentFont
    val data = model.data.value
    val type = data.types
    val currentQuestion = model.currentQuestion.intValue

    BottomAppBar(
        containerColor = NavigationBarDefaults.containerColor,
        contentColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (model.gameData.gameEnded.value) {
                GameEnded(
                    model = model,
                    font = font.value,
                    gameData = gameData,
                    activity = activity,
                    alert = alert)
            } else {
                GameOngoing(
                    model = model,
                    font = font.value,
                    data = data,
                    type = type,
                    currentQuestion = currentQuestion
                )
            }
        }
    }
}

@Composable
fun GameEnded(model: QuizzViewModel, font: List<Int>, gameData: QuizzViewModel.GameData, activity: Activity?, alert: MutableState<Boolean>) {
    Button(
        onClick = { activity?.finish() },
    ) {
        DisplayText(text = "Accueil", font = font)
    }
    Button(onClick = {
        if (model.energy.intValue == 0) {
            alert.value = true
            return@Button
        }
        model.resetStats(gameData)
    }) {
        DisplayText(text = "Rejouer", font = font)
    }
}

@Composable
fun GameOngoing(model: QuizzViewModel, font: List<Int>, data: QuizzViewModel.Data, type: List<Int>, currentQuestion: Int) {
    if (!type.isEmpty() && type[currentQuestion] != 0) {
        if (type[currentQuestion] != 4) {
            BottomBarResetButton(model = model, font = font, type = type, currentQuestion = currentQuestion)
        } else {
            Spacer(modifier = Modifier.size(1.dp))
        }
        Button(
            onClick = {
                model.updateGame(
                    answerList = if (data.types[currentQuestion] == 1) {
                        listOf(model.textFieldAnswer.value)
                    } else {
                        model.buildAnswer
                    },
                    goodAnswers = data.answers[currentQuestion].answer.toList(),
                    type = type[currentQuestion]
                )
            }
        ) {
            DisplayText(text = "Valider", font = font)
        }
    }
}

@Composable
fun BottomBarResetButton(model: QuizzViewModel, font: List<Int>, type: List<Int>, currentQuestion: Int) {
    Button(
        onClick = {
            val currentQuestionType = type[currentQuestion]
            when (currentQuestionType) {
                1 -> {
                    model.textFieldAnswer.value = ""
                }
                2, 3 -> {
                    model.resetDragDrop()
                }
            }
        }
    ) {
        DisplayText(text = "Reset", font = font)
    }
}