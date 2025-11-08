package fr.uparis.chenliu.projet.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import fr.uparis.chenliu.projet.navigation.Screen
import fr.uparis.chenliu.projet.ui.theme.ProjetTheme
import fr.uparis.chenliu.projet.ui.viewmodel.MainMenuViewModel
import kotlin.jvm.java

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

////////////////////////////////////////////////////////////////////////////////////////////////////
/*                                           Main Screen                                          */
////////////////////////////////////////////////////////////////////////////////////////////////////

@Composable
fun MyScreenMainMenu(username: String, model: MainMenuViewModel = viewModel()) {
    val backStack = rememberNavBackStack(Screen.Home)
    val snack = remember { SnackbarHostState() }

    model.init(username)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { MonTopBar() },
        bottomBar = { MonBottomBar(model = model, backStack = backStack) },
        snackbarHost = { SnackbarHost(hostState = snack) }
    )
    { pad ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(paddingValues = pad),
            entryProvider = entryProvider{
                entry<Screen.Profile> { ProfileScreen(model = model, username = username)}
                entry<Screen.Leaderboard> { LeaderboardScreen(model = model) }
                entry<Screen.Home> { HomeScreen(model = model, backStack = backStack)}
                entry<Screen.Shop> { ShopScreen(model = model) }
                entry<Screen.Settings> { SettingsScreen(model = model) }

                entry<Screen.Level> { SelectLevel(model = model, backStack) }
            }
        )
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////
/*                                         Screen Content                                         */
////////////////////////////////////////////////////////////////////////////////////////////////////

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonTopBar() = TopAppBar(
    title = { Text(text = "WTF QUIZ", style = MaterialTheme.typography.displayLarge) },
    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
)

@Composable
fun MonBottomBar(model: MainMenuViewModel, backStack: NavBackStack<NavKey>) {
    val current = backStack.last()
    var alert by remember { mutableStateOf(false) }
    var destination by remember { mutableStateOf<Screen?>(null) }

    NavigationBar {
        listOf(Screen.Profile, Screen.Leaderboard, Screen.Home, Screen.Shop, Screen.Settings).forEach { it ->
            NavigationBarItem(
                selected = current == it,
                onClick = {
                    if (current == it) {
                        return@NavigationBarItem
                    }

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
            onConfirm = {
                model.updateSettings()
                leave()
            },
            onCancel = {
                model.undoSettings()
                leave()
            },
            title = "Changements Non Sauvegardés",
            "Des modifications n'ont pas été enregistrées. Voulez-vous les conserver?"
        )
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////
/*                                       Navigation Screen                                        */
////////////////////////////////////////////////////////////////////////////////////////////////////

@Composable
fun ProfileScreen(model: MainMenuViewModel, username: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = model.translateBackground()),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column {
            Text("PROFILE")
            Text("Hello $username")
        }
    }
}

@Composable
fun LeaderboardScreen(model: MainMenuViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = model.translateBackground()),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Text("LEADERBOARD")
    }
}

@Composable
fun HomeScreen(model: MainMenuViewModel, backStack: NavBackStack<NavKey>) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = model.translateBackground()),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column {
            Text("HOME")
            Button(onClick = {
                if (backStack.last() != Screen.Level) backStack += Screen.Level
            }) { Text("Choisir la difficulté") }
        }
    }
}

@Composable
fun ShopScreen(model: MainMenuViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = model.translateBackground()),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Text("SHOP")
    }
}

@Composable
fun SettingsScreen(model: MainMenuViewModel) {
    val update = { model.updateSettings(); model.saved.value = true }
    val selectedBackground = model.background
    val selectedFont = model.font

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = model.translateBackground()),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column {
            Text("Paramètre")
            Row {
                Text("Musique")
                SliderManage(value = model.music.floatValue) {
                    model.music.floatValue = it
                    model.saved.value = false
                }
            }
            Row {
                Text("Son")
                SliderManage(value = model.sound.floatValue) {
                    model.sound.floatValue = it
                    model.saved.value = false
                }
            }
            Row {
                Text("Fond")
                ChoiceField(
                    label = "Fond",
                    choice = selectedBackground.intValue,
                    onValueChange = { newValue ->
                        selectedBackground.intValue = newValue; model.saved.value = false
                    },
                    choiceList = model.backgroundList
                )
            }
            Row {
                Text("Police")
                ChoiceField(
                    label = "Police",
                    choice = selectedFont.intValue,
                    onValueChange = { newValue -> selectedFont.intValue = newValue; model.saved.value = false },
                    choiceList = model.fontList
                )
            }
            Row {
                Text("Domaine préféré")
                WriteField(
                    value = model.preferredTopics.value,
                    onValueChange = {
                        model.preferredTopics.value = it; model.saved.value = false
                    },
                    label = "Entrer votre domaine préféré",
                    modifier = Modifier.padding(start = 10.dp),
                    fontSize = 18.sp,
                    onDone = { update() },
                    onlyNb = false
                )
            }
            Button(onClick = { update() }) { Text("Enregistrer") }
        }
    }
}

@Composable
fun SelectLevel(model: MainMenuViewModel, backStack: NavBackStack<NavKey>) {
    val difficulty = listOf("Facile", "Moyen", "Difficile", "Expert")
    val selected = remember { mutableStateOf(difficulty[0]) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = model.translateBackground()),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column {
            Text("SELECT LEVEL")
            Row {
                Button(onClick = { selected.value = difficulty[0] }) { Text("Facile") }
                Button(onClick = { selected.value = difficulty[1] }) { Text("Moyen") }
                Button(onClick = { selected.value = difficulty[2] }) { Text("Difficile") }
            }
            Button(onClick = { selected.value = difficulty[3] }) { Text("Expert") }
            Row {
                Button(onClick = {
                    if (backStack.last() != Screen.Home)
                        backStack += Screen.Home
                }) { Text("Retour") }

                val context = LocalContext.current
                val intent = Intent(context, QuizzActivity::class.java)
                intent.putExtra("difficulty", selected.value)
                Button(onClick = { context.startActivity(intent)  }) { Text("Jouer") }
            }
        }
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////
/*                                            Functions                                           */
////////////////////////////////////////////////////////////////////////////////////////////////////

@Composable
fun SliderManage(value: Float, onValueChange: (Float) -> Unit) {
    Column {
        Slider(
            value = value,
            onValueChange = onValueChange
        )
        Text(("%.0f / 100".format(value * 100)))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoiceField(label: String, choice: Int, onValueChange: (Int) -> Unit, choiceList: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    val text = choiceList[choice]

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = text,
            onValueChange = { },
            label = { Text(label + expanded) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            choiceList.forEachIndexed { index, c ->
                DropdownMenuItem(
                    onClick = {
                        onValueChange(index)
                        expanded = false
                    },
                    text = { Text(c) }
                )
            }
        }
    }
}

@Composable
fun Alert(onConfirm: () -> Unit, onCancel: () -> Unit, title: String, text: String) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Oui")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text("Non")
            }
        }
    )
}