package fr.uparis.chenliu.projet.ui.menu

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import fr.uparis.chenliu.projet.navigation.Screen
import fr.uparis.chenliu.projet.ui.viewmodel.MenuViewModel

@Composable
fun BottomBar(model: MenuViewModel, backStack: NavBackStack<NavKey>) {
    val current = backStack.last()
    var alert by remember { mutableStateOf(false) }
    var destination by remember { mutableStateOf<Screen?>(null) }
    val currentFont = model.intToFont()

    NavigationBar {
        listOf(Screen.Profile, Screen.Leaderboard, Screen.Home, Screen.Shop, Screen.Settings).forEach { it ->
            NavigationBarItem(
                selected = current == it,
                onClick = {
                    if (current == it) {
                        return@NavigationBarItem
                    }
                    backStack.clear()
                    if (!model.saved.value) {
                        destination = it
                        alert = true
                    } else {
                        backStack += it
                    }
                },
                icon = { Icon(imageVector = it.icon, contentDescription = "$it") }
            )
        }
    }

    if (alert && destination != null) {
        val leave = {
            model.saved.value = true
            backStack += destination!!
            destination = null
            alert = false
        }
        Alert(
            list = currentFont,
            onConfirm = {
                if (current == Screen.Settings) {
                    model.updateSettings()
                } else if (current == Screen.Profile) {
                    model.updateUsername()
                }
                leave()
            },
            onCancel = {
                if (current == Screen.Settings) {
                    model.undoSettings()
                } else if (current == Screen.Profile) {
                    model.undoUsername()
                }
                leave()
            },
            title = "Changements Non Sauvegardés",
            text = "Des modifications n'ont pas été enregistrées. Voulez-vous les conserver?",
            onConfirmText = "Oui",
            onCancelText = "Non"
        )
    }
}