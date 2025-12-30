package fr.uparis.chenliu.projet.ui.menu

import android.content.Context
import android.content.Intent
import android.content.res.Resources
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    val context = LocalContext.current
    val alert = remember { mutableStateOf(false) }
    val difficultyList = model.difficultyList
    val subjectList by model.subjectList.collectAsState()
    val subjectImage by model.subjectImageList.collectAsState()
    val currentBackground = model.currentBackground
    val currentFont = model.currentFont
    val selectedDifficulty = remember { mutableStateOf(difficultyList[0]) }
    val selectedSubject = if (subjectList.isEmpty()) {
        remember { mutableStateOf("") }
    } else {
        remember { mutableStateOf(subjectList[0]) }
    }

    if (alert.value) {
        HomeScreenAlert(currentFont = currentFont.value, alert = alert, backStack = backStack)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        DisplayBackground(currentBackground = currentBackground.intValue)
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HomeScreenTitle(currentFont = currentFont.value)
            DifficultyContent(
                difficultyList = difficultyList,
                selectedDifficulty = selectedDifficulty,
                currentFont = currentFont.value
            )
            SubjectContent(
                subjectList = subjectList,
                selectedSubject = selectedSubject,
                currentFont = currentFont.value,
                subjectImage = subjectImage,
                context = context,
                modifier = Modifier.weight(1f)
            )
            HomeScreenButton(
                model = model,
                selectedDifficulty = selectedDifficulty,
                selectedSubject = selectedSubject,
                currentFont = currentFont.value,
                alert = alert,
                context = context
            )
        }
    }
}

fun sendNotification(model: MenuViewModel, context: Context) {
    val energy = model.energy
    val subscribe = model.subscription
    val lastTimeEnergy = model.lastTimeEnergy
    val now = System.currentTimeMillis()
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

@Composable
fun HomeScreenTitle(currentFont: List<Int>) {
    DisplayText(text = "WTF QUIZ", font = currentFont, fontSize = 24.sp)
}

@Composable
fun DifficultyContent(difficultyList: List<String>, selectedDifficulty: MutableState<String>, currentFont: List<Int>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        difficultyList.forEach { difficulty ->
            Button(
                onClick = { selectedDifficulty.value = difficulty },
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedDifficulty.value == difficulty) Color.Blue else Color.Gray,
                    contentColor = Color.White
                )
            ) {
                DisplayText(text = difficulty, font = currentFont)
            }
        }
    }
}

@Composable
fun SubjectContent(subjectList: List<String>, selectedSubject: MutableState<String>, currentFont: List<Int>, subjectImage: List<String>, context: Context, modifier: Modifier) {
    val resources = remember(context) { context.resources }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
    ) {
        items(subjectList) { subject ->
            val isSelected = selectedSubject.value == subject
            Button(
                onClick = { selectedSubject.value = subject },
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
                    SubjectImage(
                        subjectList = subjectList,
                        subjectImage = subjectImage,
                        subject = subject,
                        context = context,
                        resources = resources
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)))
                    }
                    DisplayText(text = subject, font = currentFont, fontSize = 24.sp)
                }
            }
        }
    }
}

@Composable
fun SubjectImage(subjectList: List<String>, subjectImage: List<String>, subject: String, context: Context, resources: Resources) {
    var imageResId by remember { mutableIntStateOf(0) }
    val index = subjectList.indexOf(subject)
    if (index >= 0 && index < subjectImage.size) {
        imageResId = resources.getIdentifier(
            subjectImage[index],
            "drawable",
            context.packageName
        )
    }
    if (imageResId != 0) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
@Composable
fun HomeScreenButton(model: MenuViewModel, selectedDifficulty: MutableState<String>, selectedSubject: MutableState<String>, currentFont: List<Int>, alert: MutableState<Boolean>, context: Context) {
    val intent = Intent(context, QuizzActivity::class.java)
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 25.dp).padding(top = 25.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                if (model.energy.intValue == 0) {
                    alert.value = true
                    return@Button
                }
                model.removeEnergy()
                intent.putExtra("difficulty", selectedDifficulty.value)
                intent.putExtra("subject", selectedSubject.value)
                context.startActivity(intent)
                sendNotification(model = model, context = context)
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            DisplayText(text = "Jouer", font = currentFont, fontSize = 18.sp)
        }
    }
}

@Composable
fun HomeScreenAlert(currentFont: List<Int>, alert: MutableState<Boolean>, backStack: NavBackStack<NavKey>) {
    Alert(
        font = currentFont,
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