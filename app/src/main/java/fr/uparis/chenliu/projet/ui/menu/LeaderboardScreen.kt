package fr.uparis.chenliu.projet.ui.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.uparis.chenliu.projet.ui.viewmodel.MenuViewModel

@Composable
fun LeaderboardScreen(model: MenuViewModel) {
    val currentBackground = model.intToBackground()
    val currentFont = model.intToFont()
    val username = model.usernameLeaderboard
    val score = model.score
    val bestTime = model.bestTime
    val changed = model.changed
    val subjectList = model.subjectList
    val currentSubject = model.subject

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = currentBackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            DisplayText(list = currentFont, text = "Classement", fontSize = 24.sp)
            LazyHorizontalGrid(
                rows = GridCells.Fixed(3), // 3 lignes
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),                      // hauteur totale pour 3 lignes
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),

            ) {
                items(subjectList.value) { subj ->
                    Button(onClick = {
                        if (currentSubject.value != subj) {
                            model.changeSubject(subj); changed.value = true
                        }
                    }, modifier = Modifier.width(LocalWindowInfo.current.containerSize.width.dp/7)
                    ) {
                        DisplayText(list = currentFont, text = subj, fontSize = 16.sp)
                    }
                }
            }

            //todo mettre une lazycolumn pour que ca soit scrollable
            if (!changed.value) {
                if (username.isEmpty()) {
                    DisplayText(
                        list = currentFont,
                        text = "Pas encore de résultat en ${currentSubject.value}"
                    )
                } else {
                    DisplayText(list = currentFont, text = currentSubject.value)
                    for (i in 0..username.size - 1) {
                        Row {
                            DisplayText(list = currentFont, text = "${i + 1} | ")
                            DisplayText(list = currentFont, text = username[i]!! + " | ")
                            DisplayText(list = currentFont, text = score[i].toString() + " | ")
                            DisplayText(list = currentFont, text = bestTime[i].toString())
                        }
                    }
                }
            }
        }
    }
}
