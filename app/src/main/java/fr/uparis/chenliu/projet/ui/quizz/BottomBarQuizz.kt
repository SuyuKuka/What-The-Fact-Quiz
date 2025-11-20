package fr.uparis.chenliu.projet.ui.quizz

import android.R.attr.onClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.uparis.chenliu.projet.ui.menu.DisplayText
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBarQuizz(gameData: QuizzViewModel.GameData, model: QuizzViewModel) {
    val font = model.translateFont()



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

            if (model.data.types.isNotEmpty()) {
                if (model.data.types[model.currentQuestion.intValue] == 2 ||
                    model.data.types[model.currentQuestion.intValue] == 3 ||
                    model.data.types[model.currentQuestion.intValue] == 4
                ) {
                    TextButton(onClick = { /* action */ }) {
                        Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                            DisplayText(list = font, text = "Reset")
                        }
                    }
                } else {
                    TextButton(onClick = { /* action */ }) {
                        Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                            DisplayText(list = font, text = "")
                        }
                    }
                }
            }

            Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                DisplayText(list = font, text = "Score: ${gameData.score.value}")
            }

            if (model.data.types.isNotEmpty()) {
                TextButton(onClick = { /* action */ }) {
                    Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                        if (model.gameData.gameEnded.value) {
                            DisplayText(list = font, text = "Rejouer")
                        } else if (
                            model.data.types[model.currentQuestion.intValue] == 2 ||
                            model.data.types[model.currentQuestion.intValue] == 3 ||
                            model.data.types[model.currentQuestion.intValue] == 4
                        ) {
                            DisplayText(list = font, text = "Valider")
                        } else {
                            TextButton(onClick = { /* action */ }) {
                                Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                                    DisplayText(list = font, text = "")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}