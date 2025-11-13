package fr.uparis.chenliu.projet.ui.quizz

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uparis.chenliu.projet.ui.menu.Alert
import fr.uparis.chenliu.projet.ui.menu.DisplayText
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel

@Composable
fun ResultScreen(
    font: List<Int>,
    gameData: QuizzViewModel.GameData,
    model: QuizzViewModel = viewModel()
) {
    val activity = LocalActivity.current
    val alert = remember { mutableStateOf(false) }

    if (alert.value) {  // alerte le manque d'energie
        Alert(
            list = font,
            onConfirm = { alert.value = false; activity?.finish() },
            onCancel = { alert.value = false },
            title = "Joue Sans Limite ! 👑",
            text =
                """
                Tu n'as plus d'énergie ! 😱
                Ne perds plus ton temps à attendre !
                Achète l'abonnement prenium pour bénéficier de l'énergie A L'INFINI et jouer sans interruption
                """,
            onConfirmText = "Continuer",
            onCancelText = "Fermer"
        )
    }

    model.insertLeaderboard()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DisplayText(list = font, text = "RESULTAT", textAlign = TextAlign.Center, fontSize = 32.sp)
        DisplayText(
            list = font,
            text = "Score : ${gameData.score.doubleValue}/10 | Temps : ${"%.3fs".format(gameData.time.doubleValue)}",
            textAlign = TextAlign.Center
        )

        LazyColumn(modifier = Modifier.fillMaxWidth().weight(4f)) {
            itemsIndexed(gameData.questionHistory) { i, question ->
                if (gameData.answerHistory[i] == gameData.goodAnswer[i]) {
                    DisplayText(list = font, text = "    Question ${i + 1}: $question", color = Color.Black)
                    DisplayText(
                        list = font,
                        "Réponse -> ${gameData.goodAnswer[i]}\n",
                        color = Color.Green
                    )
                } else {
                    DisplayText(list = font, text = "  Question ${i + 1}: $question", color = Color.Black)
                    DisplayText(
                        list = font,
                        text = "Faux : ${gameData.answerHistory[i]}",
                        color = Color.Red
                    )
                    DisplayText(
                        list = font,
                        text = "Réponse -> ${gameData.goodAnswer[i]}\n",
                        color = Color.Blue
                    )
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().weight(2f), horizontalArrangement = Arrangement.Center){
            Button(
                onClick = { activity?.finish() },
                modifier = Modifier.padding(8.dp)
            ) {
                DisplayText(list = font, text = "Accueil")
            }
            Button(
                onClick = {
                    if (model.energy.intValue == 0) {
                        alert.value = true
                        return@Button
                    }
                    model.resetStats(gameData)
                },
                modifier = Modifier.padding(8.dp)
            ) {
                DisplayText(list = font, text = "Rejouer")
            }
        }
    }
}