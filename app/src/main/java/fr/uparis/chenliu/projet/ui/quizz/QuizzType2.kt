package fr.uparis.chenliu.projet.ui.quizz

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.uparis.chenliu.projet.ui.menu.DisplayText
import fr.uparis.chenliu.projet.ui.viewmodel.QuizzViewModel

@Composable
fun Quizztype2(
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
        items(data.answers[currentQuestion.intValue].propositions) { ans ->
            Box (
                modifier = Modifier
                    .aspectRatio(1.5f).padding(8.dp).fillMaxWidth()
                    .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
            ) {
                DropTargetImage(
                    intValue = data.answers[currentQuestion.intValue].propositions.indexOf(ans),
                    font = font,
                    model = model,
                    type = 2
                )
            }
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(data.answers[currentQuestion.intValue].propositions) { ans ->
            Box (
                modifier = Modifier
                    .aspectRatio(1.5f).padding(8.dp).fillMaxWidth()
                    .background(Color.Black),
                contentAlignment = Alignment.BottomCenter
            ) {
                DragImage(answer = ans[0], url = ans[1])
                DisplayText(list = font, text = ans[0], fontSize = 24.sp, color = Color.Red)
            }
        }
    }
}