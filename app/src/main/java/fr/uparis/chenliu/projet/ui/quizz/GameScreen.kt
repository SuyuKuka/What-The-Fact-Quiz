package fr.uparis.chenliu.projet.ui.quizz

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uparis.chenliu.projet.R
import fr.uparis.chenliu.projet.ui.menu.DisplayText
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel

@Composable
fun GameScreen(
    data: QuizzViewModel.Data,
    model: QuizzViewModel = viewModel()
){
    val currentQuestion = model.currentQuestion
    val font = model.currentFont
    val typeQuestion = data.types[currentQuestion.intValue]
    val height = if (typeQuestion == 0 || typeQuestion == 1) {
        0.5f
    } else {
        0.20f
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_default_blurred),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .fillMaxHeight(height)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DisplayText(
                    list = font.value,
                    text = data.question[currentQuestion.intValue],
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
            when (typeQuestion) {
                0 -> Quizztype0(
                    font = font.value,
                    data = data,
                    model = model,
                    modifier = Modifier.weight(weight = 1f)
                )

                1 -> Quizztype1(
                    font = font.value,
                    model = model,
                    data = data
                )

                2 -> Quizztype2(
                    font = font.value,
                    data = data,
                    model = model,
                    modifier = Modifier.weight(weight = 1f)
                )

                3 -> Quizztype3(
                    font = font.value,
                    data = data,
                    model = model,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(weight = 1f)
                        .padding(start = 16.dp)
                        .padding(end = 16.dp)
                )

                4 -> Quizztype4(
                    font = font.value,
                    data = data,
                    model = model,
                    Modifier
                        .fillMaxWidth()
                        .weight(weight = 1f)
                        .padding(start = 16.dp)
                        .padding(end = 16.dp)
                )
            }
        }
    }
}
