package fr.uparis.chenliu.projet.ui.quizz

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.uparis.chenliu.projet.ui.menu.DisplayText
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBarQuizz(gameData: QuizzViewModel.GameData, model: QuizzViewModel) {
    val font = model.translateFont()
    val activity = LocalActivity.current
    val alert = remember { mutableStateOf(false) }
    val data = model.data.value
    val type = data.types
    val currentQuestion = model.currentQuestion.intValue

    BottomAppBar(
        containerColor = Color.DarkGray,
        contentColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (model.gameData.gameEnded.value) {
                Button(
                    onClick = { activity?.finish() },
                ) {
                    DisplayText(list = font, text = "Accueil")
                }
                Button(onClick = {
                    if (model.energy.intValue == 0) {
                        alert.value = true
                        return@Button
                    }
                    model.resetStats(gameData)
                }) {
                    DisplayText(list = font, text = "Rejouer")
                }
            } else {
                if (!type.isEmpty()) {
                    if (type[currentQuestion] != 0) {
                        Button(
                            onClick = {
                                val currentQuestionType = type[currentQuestion]
                                when (currentQuestionType) {
                                    1 -> { model.textFieldQuestion.value = "" }
                                    2 -> { model.resetDragDrop() }
                                    3, 4 -> { model.currentImage.intValue = 0 }
                                }
                            }
                        ) {
                            DisplayText(list = font, text = "Reset")
                        }
                        Button(
                            onClick = {
                                model.updateGame(
                                    answerList = if (data.types[currentQuestion] == 1) {
                                        listOf(model.textFieldQuestion.value)
                                    } else {
                                        model.buildAnswer
                                    },
                                    verify = data.answers[currentQuestion].answer.toList(),
                                    type = type[currentQuestion]
                                )
                            }
                        ) {
                            DisplayText(list = font, text = "Valider")
                        }
                    }
                }
            }
        }
    }
}