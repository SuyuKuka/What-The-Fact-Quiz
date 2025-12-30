package fr.uparis.chenliu.projet.ui.menu

import android.content.Context
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import fr.uparis.chenliu.projet.navigation.Screen
import fr.uparis.chenliu.projet.ui.viewmodel.MenuViewModel

@Composable
fun BottomBarMenu(model: MenuViewModel, backStack: NavBackStack<NavKey>) {
    val currentScreen = backStack.last()
    var alert by remember { mutableStateOf(false) }
    var destination by remember { mutableStateOf<Screen?>(null) }
    val currentFont = model.currentFont
    val context = LocalContext.current
    val screen = listOf(Screen.Profile, Screen.Leaderboard, Screen.Home, Screen.Shop, Screen.Settings)

    NavigationBar {
        screen.forEach { it ->
            NavigationBarItem(
                selected = currentScreen == it,
                onClick = {
                    if (currentScreen == it) {
                        return@NavigationBarItem
                    }
                    backStack.clear()
                    backStack.add(currentScreen)
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
        BottomBarAlert(
            model = model,
            currentScreen = currentScreen,
            currentFont = currentFont.value,
            context = context,
            leave = leave
        )
    }
}

@Composable
fun BottomBarAlert(model: MenuViewModel, currentScreen: NavKey, currentFont: List<Int>, context: Context, leave: () -> Unit) {
    Alert(
        font = currentFont,
        onConfirm = {
            if (currentScreen == Screen.Settings) {
                model.updateSettings()
            } else if (currentScreen == Screen.Profile) {
                model.updateUsername()
            }
            leave()
        },
        onCancel = {
            if (currentScreen == Screen.Settings) {
                model.undoSettings(context)
            } else if (currentScreen == Screen.Profile) {
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