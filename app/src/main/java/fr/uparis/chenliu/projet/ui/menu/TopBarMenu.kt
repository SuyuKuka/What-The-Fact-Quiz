package fr.uparis.chenliu.projet.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val currentFont = model.intToFont()
    var textEnergy by remember { mutableStateOf("") }

    TopAppBar(
        title = {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(onClick = {
                    backStack.clear()
                    backStack.add(Screen.Profile)
                }) {
                    DisplayText(
                        list = currentFont,
                        text = model.username.value,
                        fontSize = 18.sp
                    )
                }
                textEnergy = if (model.subscription.value) {
                    "∞/20⚡"
                } else {
                    if (energy == 20) {
                        "$energy/20⚡"
                    } else {
                        "$energy/20⚡ ($timeEnergy)"
                    }
                }
                Card(onClick = {
                    backStack.clear()
                    backStack.add(Screen.Shop)
                }) {
                    DisplayText(
                        list = currentFont,
                        text = textEnergy,
                        fontSize = 18.sp
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    )
}