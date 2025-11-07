package fr.uparis.chenliu.projet.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import fr.uparis.chenliu.projet.navigation.Screen
import fr.uparis.chenliu.projet.ui.theme.ProjetTheme
import fr.uparis.chenliu.projet.ui.viewmodel.MainViewModel

class MainMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjetTheme {
                val username = intent.getStringExtra("username")!!
                if (username == "") {
                    finish()
                }
                MyScreenMainMenu(username)
            }
        }
    }
}

@Composable
fun MyScreenMainMenu(username: String, model: MainViewModel = viewModel()) {
    val backStack = rememberNavBackStack(Screen.Home)
    val snack = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { MonTopBar() },
        bottomBar = { MonBottomBar(backStack) },
        snackbarHost = { SnackbarHost(snack) }
    ) { pad ->
            NavDisplay(
                backStack = backStack,
                modifier = Modifier.padding(pad),
                entryProvider = entryProvider{
                    entry<Screen.Home> { HomeScreen()}
                    entry<Screen.Profile> { ProfileScreen()}
                    entry<Screen.Leaderboard> { LeaderboardScreen() }
                    entry<Screen.Shop> { ShopScreen() }
                    entry<Screen.Settings> { SettingsScreen() }
                }
            )
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonTopBar() = TopAppBar(
    title = { Text("WTF QUIZ", style = MaterialTheme.typography.displayLarge) },
    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
)

@Composable
fun MonBottomBar(backStack: NavBackStack<NavKey>)= NavigationBar{
    val current=backStack.last()
    listOf(Screen.Home, Screen.Profile, Screen.Leaderboard, Screen.Shop, Screen.Settings).forEach{
        NavigationBarItem(
            selected = current==it,
            onClick = {
                if (backStack.last()!=it)
                    backStack+=it
            },
            icon = { Icon(it.icon, contentDescription = "$it")}
        )
    }
}

@Composable
fun HomeScreen() {
    Text("HOME")
}

@Composable
fun ProfileScreen() {
    Text("PROFILE")
}

@Composable
fun LeaderboardScreen() {
    Text("LEADERBOARD")
}

@Composable
fun ShopScreen() {
    Text("SHOP")
}

@Composable
fun SettingsScreen() {
    Text("SETTINGS")
}
