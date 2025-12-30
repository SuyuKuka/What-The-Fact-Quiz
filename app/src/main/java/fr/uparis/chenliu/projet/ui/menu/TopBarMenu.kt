package fr.uparis.chenliu.projet.ui.menu

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import fr.uparis.chenliu.projet.navigation.Screen
import fr.uparis.chenliu.projet.ui.viewmodel.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarMenu(model: MenuViewModel, backStack: NavBackStack<NavKey>) {
    val energy = model.energy.intValue
    val timeEnergy = model.timeEnergy.value
    val currentFont = model.currentFont
    val context = LocalContext.current

    TopAppBar(
        title = {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TopBarPlayer(model = model, font = currentFont.value, backStack = backStack)
                TopBarEnergy(
                    model = model,
                    font = currentFont.value,
                    context = context,
                    energy = energy,
                    timeEnergy = timeEnergy
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    )
}

fun energyColor(energy: Int, maxEnergy: Int = 20): Color {
    val ratio = energy.toFloat() / maxEnergy.toFloat()
    val red = (255 * (1f - ratio)).toInt()
    val green = (255 * ratio).toInt()

    return Color(red, green, 0)
}

@Composable
fun TopBarPlayer(model: MenuViewModel, font: List<Int>, backStack: NavBackStack<NavKey>) {
    Button (onClick = {
        backStack.clear()
        backStack.add(Screen.Profile)
    }) {
        DisplayText(
            text = model.username.value,
            font = font,
            fontSize = 18.sp
        )
    }
}

@Composable
fun TopBarEnergy(model: MenuViewModel, font: List<Int>, context: Context, energy: Int, timeEnergy: String) {
    Button(onClick = {
        if (!model.subscription.value && energy < 20) {
            Toast.makeText(
                context,
                "Prochain point d’énergie dans $timeEnergy",
                Toast.LENGTH_SHORT
            ).show()
        } },
        colors = ButtonDefaults.buttonColors(containerColor =
            if (model.subscription.value) {
                Color.Cyan
            } else if (energy == 0) {
                breathingRed()
            } else {
                energyColor(energy)
            }
        ),
        modifier = Modifier.padding(end = 16.dp)
    ) {
        DisplayText(
            text = if (model.subscription.value) "∞" else "$energy",
            font = font,
            fontSize = 18.sp
        )
        Icon(
            imageVector = Icons.Default.Bolt,
            contentDescription = "Energy",
            tint = Color.Yellow
        )
    }
}

@Composable
fun breathingRed(): Color {
    val infiniteTransition = rememberInfiniteTransition(label = "blink")
    val alpha = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blinkAlpha"
    )
    return Color.Red.copy(alpha = alpha.value)
}