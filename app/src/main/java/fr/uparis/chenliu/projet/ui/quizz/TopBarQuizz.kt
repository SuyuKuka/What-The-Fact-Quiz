package fr.uparis.chenliu.projet.ui.quizz

import androidx.compose.foundation.layout.Arrangement
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
import fr.uparis.chenliu.projet.ui.menu.DisplayText
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarQuizz(gameData: QuizzViewModel.GameData, model: QuizzViewModel) {
    val font = model.translateFont()

    TopAppBar(
        title = {
            Row(modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DisplayText(list = font, text = "Score: ${gameData.score.intValue} ")
                DisplayText(list = font, text = "Temps: " + "%.3fs".format(gameData.time.doubleValue))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Red) // tmp
    )
}