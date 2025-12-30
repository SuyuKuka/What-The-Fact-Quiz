package fr.uparis.chenliu.projet.ui.quizz

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import fr.uparis.chenliu.projet.ui.menu.Alert
import fr.uparis.chenliu.projet.ui.menu.DisplayBackground
import fr.uparis.chenliu.projet.ui.menu.DisplayText
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel

@Composable
fun ResultScreen(
    model: QuizzViewModel,
    font: List<Int>,
    background: Int,
    gameData: QuizzViewModel.GameData,
) {
    val activity = LocalActivity.current
    val alert = remember { mutableStateOf(false) }
    model.insertLeaderboard()

    Box(modifier = Modifier.fillMaxSize()) {
        DisplayBackground(currentBackground = background)
        ResultContent(font = font, gameData = gameData)
        Alert(font = font, alert = alert, activity = activity)
    }
}

@Composable
fun ResultContent(font: List<Int>, gameData: QuizzViewModel.GameData) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ResultTitle(font = font)
        Result(font = font, gameData = gameData)
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(4f)) {
            itemsIndexed(gameData.questionHistory) { index, question ->
                GoodAndWrongAnswers(font = font, gameData = gameData, index = index, question = question)
            }
        }
    }
}

@Composable
fun ResultTitle(font: List<Int>) {
    DisplayText(
        text = "RESULTAT",
        font = font,
        textAlign = TextAlign.Center,
        fontSize = 32.sp
    )
}

@Composable
fun Result(font: List<Int>, gameData: QuizzViewModel.GameData) {
    DisplayText(
        text = "Score : ${gameData.score.intValue} | Temps : ${"%.3fs".format(gameData.time.doubleValue)}",
        font = font,
        textAlign = TextAlign.Center
    )
}

@Composable
fun GoodAndWrongAnswers(font: List<Int>, gameData: QuizzViewModel.GameData, index: Int, question: String) {
    val check = gameData.answerHistory[index].lowercase() == gameData.goodAnswer[index].lowercase()
    DisplayText(
        text = "    Question ${index + 1}: $question",
        font = font,
        color = Color.Black
    )
    DisplayText(
        text = if (check) "Réponse -> ${gameData.goodAnswer[index]}\n"
                else "Faux : ${gameData.answerHistory[index]}",
        font = font,
        color = if (check) Color.Green else Color.Red
    )
    if (!check) {
        DisplayText(
            text = "Réponse -> ${gameData.goodAnswer[index]}\n",
            font = font,
            color = Color.Blue
        )
    }
}


@Composable
fun Alert(font: List<Int>, alert: MutableState<Boolean>, activity: Activity?) {
    if (alert.value) {
        Alert(
            font = font,
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
}