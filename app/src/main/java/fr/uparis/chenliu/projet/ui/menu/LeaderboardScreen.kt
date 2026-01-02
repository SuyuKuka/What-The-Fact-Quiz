package fr.uparis.chenliu.projet.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.uparis.chenliu.projet.ui.viewmodel.MenuViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

@Composable
fun LeaderboardScreen(model: MenuViewModel) {
    val currentBackground = model.currentBackground
    val currentFont = model.currentFont
    val usernameList = model.usernameLeaderboard
    val score = model.score
    val bestTime = model.bestTime
    val updated = model.updated
    val subjectList = model.subjectList
    val currentSubject = model.subject

    Box(modifier = Modifier.fillMaxSize()) {
        DisplayBackground(currentBackground = currentBackground.intValue)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            LeaderboardTitle(font = currentFont.value)
            SubjectList(
                model = model,
                subjectList = subjectList.collectAsState().value,
                currentSubject = currentSubject.value,
                currentFont = currentFont.value,
                updated = updated
            )
            LeaderboardList(
                usernameList = usernameList,
                score = score,
                bestTime = bestTime,
                currentFont = currentFont.value,
                updated = updated.value,
                currentSubject = currentSubject.value
            )
        }
    }
}

@Composable
fun LeaderboardTitle(font: List<Int>) {
    Spacer(modifier = Modifier.height(24.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        DisplayText(text = "Classement", font = font, fontSize = 30.sp)
    }
}

@Composable
fun SubjectList(model: MenuViewModel, subjectList: List<String>, currentSubject: String, currentFont: List<Int>, updated: MutableState<Boolean>) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(1),
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f))
            .height(40.dp)
            .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(subjectList) { subject ->
            Button(onClick = {
                if (currentSubject != subject) updated.value = false
                model.updateLeaderboard(subject)
            }, modifier = Modifier.fillMaxWidth(0.7f),
                colors = ButtonDefaults.buttonColors(
                     if (currentSubject == subject) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryFixed,
                )
            ) {
                DisplayText(text = subject, font = currentFont, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun LeaderBoardHead(currentFont: List<Int>) {
    Row(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PositionDisplay(
            text = "Top",
            font = currentFont,
            modifier = Modifier.weight(0.15f)
        )
        PositionDisplay(
            text = "Nom",
            font = currentFont,
            modifier = Modifier.weight(0.45f)
        )
        PositionDisplay(
            text = "Score",
            font = currentFont,
            modifier = Modifier.weight(0.20f)
        )
        PositionDisplay(
            text = "Temps",
            font = currentFont,
            modifier = Modifier.weight(0.20f)
        )
    }
}

@Composable
fun LeaderboardList(usernameList: List<String?>, score: List<Int?>, bestTime: List<Double?>, currentFont: List<Int>, updated: Boolean, currentSubject: String) {
    if (usernameList.isNotEmpty() && updated) {
        LeaderBoardHead(currentFont)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f), shape = RoundedCornerShape(16.dp))
        ) {
            LazyColumn {
                itemsIndexed(usernameList) { index, username ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PositionDisplay(
                            text = "${index + 1}",
                            font = currentFont,
                            modifier = Modifier
                                .weight(0.15f)
                                .padding(15.dp)
                        )
                        PositionDisplay(
                            text = username!!,
                            font = currentFont,
                            modifier = Modifier.weight(0.45f)
                        )
                        PositionDisplay(
                            text = "${score[index] ?: 0}",
                            font = currentFont,
                            modifier = Modifier.weight(0.20f)
                        )
                        PositionDisplay(
                            text = "${bestTime[index] ?: 0.0}s",
                            font = currentFont,
                            modifier = Modifier.weight(0.20f)
                        )
                    }
                    HorizontalDivider(color = Color.Gray, thickness = 0.4.dp)
                }
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(vertical = 12.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            DisplayText(
                text = "Pas encore de résultat en $currentSubject",
                font = currentFont,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PositionDisplay(text: String, font: List<Int>, modifier: Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        DisplayText(text = text, font = font, fontSize = 16.sp)
    }
}