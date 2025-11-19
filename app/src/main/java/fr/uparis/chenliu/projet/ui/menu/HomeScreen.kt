package fr.uparis.chenliu.projet.ui.menu

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import fr.uparis.chenliu.projet.navigation.Screen
import fr.uparis.chenliu.projet.ui.quizz.QuizzActivity
import fr.uparis.chenliu.projet.ui.viewmodel.MenuViewModel

@Composable
fun HomeScreen(model: MenuViewModel, backStack: NavBackStack<NavKey>) {
    val alert = remember { mutableStateOf(false) }
    val difficulty = model.difficultyList
    val subject = model.subjectList
    val energy = model.energy
    val subscribe = model.subscription
    val lastTimeEnergy = model.lastTimeEnergy
    val now = System.currentTimeMillis()

    val selectedDiff = remember { mutableStateOf(difficulty[0]) }
    val selectedSubj = remember { mutableStateOf(subject[0]) }
    val context = LocalContext.current
    val intent = Intent(context, QuizzActivity::class.java)
    intent.putExtra("difficulty", selectedDiff.value)
    intent.putExtra("subject", selectedSubj.value)

    val currentBackground = model.intToBackground()
    val currentFont = model.intToFont()

    val sendNotification = {
        if (!subscribe.value) {
            val remainingTime = 120L - ((now - lastTimeEnergy.longValue) / 1000L)
            if (energy.intValue == 0) {
                gainEnergyRemind(context = context, remainingTime = remainingTime)
            }
            if (energy.intValue < 20) {
                fullEnergyRemind(
                    context = context,
                    currentEnergy = energy.intValue,
                    remainingTime = remainingTime
                )
            }
        }
    }

    if (alert.value) {
        Alert(
            list = currentFont,
            onConfirm = {
                alert.value = false
                backStack += Screen.Shop
            },
            onCancel = {
                alert.value = false
            },
            title = "Joue Sans Limite ! 👑",
            text = "Tu n'as plus d'énergie ! 😱\nNe perds plus ton temps à attendre !\nAchète l'abonnement prenium pour bénéficier de l'énergie A L'INFINI et jouer sans interruption",
            onConfirmText = "Je m'abonne",
            onCancelText = "Fermer"
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = currentBackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DisplayText(list = currentFont, text = "WTF QUIZ", fontSize = 24.sp)
            Row(modifier = Modifier.fillMaxWidth()) {
                difficulty.forEach { diff ->
                    Button(
                        onClick = { selectedDiff.value = diff },
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedDiff.value == diff) Color.Blue else Color.Gray,
                            contentColor = Color.White
                        )
                    ) {
                        DisplayText(list = currentFont, text = diff)
                    }
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .weight(1f)
            ) {
                items(subject) { subj ->
                    val isSelected = selectedSubj.value == subj
                    Button(
                        onClick = { selectedSubj.value = subj },
                        modifier = Modifier
                            .aspectRatio(1.2f)
                            .padding(8.dp)
                            .fillMaxWidth(),
                        shape = RectangleShape,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            Image(
                                painter = painterResource(id = model.subjectImage(subj)),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            if (isSelected) {
                                Spacer(
                                    modifier = Modifier.fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.2f))
                                )
                            }
                            DisplayText(list = currentFont, text = subj, fontSize = 24.sp)
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        if (model.energy.intValue == 0) {
                            alert.value = true
                            return@Button
                        }
                        model.removeEnergy()
                        context.startActivity(intent)
                        sendNotification()
                    },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    DisplayText(list = currentFont, text = "Jouer", fontSize = 18.sp)
                }
            }
        }
    }
}