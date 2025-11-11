package fr.uparis.chenliu.projet.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import fr.uparis.chenliu.projet.ui.viewmodel.MenuViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(model: MenuViewModel) {
    val energy = model.energy.intValue
    val timeEnergy = model.timeEnergy.value
    val currentFont = model.intToFont()

    TopAppBar(
        title = {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    DisplayText(list = currentFont, text = "Pseudo : ${model.username.value}", fontSize = 18.sp)
                    if (model.subscription.value) {
                        DisplayText(list = currentFont, text = "Energie : ∞/20⚡")
                    } else {
                        if (energy == 20) {
                            DisplayText(list = currentFont, text = "Energie : $energy/20⚡")
                        } else {
                            DisplayText(list = currentFont, text = "Energie : $energy/20⚡ ($timeEnergy)")
                        }
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    )
}