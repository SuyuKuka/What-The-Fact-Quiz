package fr.uparis.chenliu.projet.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import fr.uparis.chenliu.projet.navigation.Screen
import fr.uparis.chenliu.projet.ui.theme.ProjetTheme
import fr.uparis.chenliu.projet.ui.viewmodel.MenuViewModel

class MainMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjetTheme {
                val username = intent.getStringExtra("username")!!
                if (username == "") {
                    finish()
                }
                MyScreenMainMenu(username = username)
            }
        }
    }
}

@Composable
fun MyScreenMainMenu(username: String, model: MenuViewModel = viewModel()) {
    val backStack = rememberNavBackStack(Screen.Home)
    val snack = remember { SnackbarHostState() }

    model.init(username)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar(model = model) },
        bottomBar = { BottomBar(model = model, backStack = backStack) },
        snackbarHost = { SnackbarHost(hostState = snack) }
    )
    { pad ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(paddingValues = pad),
            entryProvider = entryProvider{
                entry<Screen.Profile> { model.getUsers(); ProfileScreen(model = model) }
                entry<Screen.Leaderboard> { LeaderboardScreen(model = model,) }
                entry<Screen.Home> { HomeScreen(model = model, backStack = backStack,) }
                entry<Screen.Shop> { ShopScreen(model = model,) }
                entry<Screen.Settings> { SettingsScreen(model = model) }
            }
        )
    }
}

@Composable
fun Alert(list: List<Int>, onConfirm: () -> Unit, onCancel: () -> Unit, title: String, text: String, onConfirmText: String, onCancelText: String) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { DisplayText(list = list, text = title) },
        text = { DisplayText(list = list, text = text) },
        confirmButton = {
            Button(onClick = onConfirm) {
                DisplayText(list = list, text = onConfirmText)
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                DisplayText(list = list, text = onCancelText)
            }
        }
    )
}

@Composable
fun DisplayText(
    list: List<Int>,
    text: String,
    fontSize: TextUnit = 16.sp,
    color: Color = Color.Black,
    textAlign: TextAlign = TextAlign.Start
) {
    Text(
        text = text,
        fontSize = (fontSize.value - list[1]).sp,
        color = color,
        fontFamily = FontFamily(Font(list[0])),
        textAlign = textAlign
    )
}