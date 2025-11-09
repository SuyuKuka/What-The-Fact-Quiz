package fr.uparis.chenliu.projet.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import fr.uparis.chenliu.projet.navigation.Screen
import fr.uparis.chenliu.projet.ui.theme.ProjetTheme
import fr.uparis.chenliu.projet.ui.viewmodel.MenuViewModel
import java.time.LocalDateTime
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
fun MyScreenMainMenu(username: String, model: MenuViewModel = viewModel()) {
    val backStack = rememberNavBackStack(Screen.Home)
    val snack = remember { SnackbarHostState() }

    model.init(username)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { MonTopBar(model = model) },
        bottomBar = { MonBottomBar(model = model, backStack = backStack) },
        snackbarHost = { SnackbarHost(hostState = snack) }
    )
    { pad ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(paddingValues = pad),
            entryProvider = entryProvider{
                entry<Screen.Profile> { model.getUsers(); ProfileScreen(model = model)}
                entry<Screen.Leaderboard> { LeaderboardScreen(model = model) }
                entry<Screen.Home> { HomeScreen(model = model)}
                entry<Screen.Shop> { ShopScreen(model = model) }
                entry<Screen.Settings> { SettingsScreen(model = model) }
            }
        )
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////
/*                                         Screen Content                                         */
////////////////////////////////////////////////////////////////////////////////////////////////////

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonTopBar(model: MenuViewModel) {
    val energy = model.energy.intValue
    val timeEnergy = model.timeEnergy.value

    TopAppBar(
        title = {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Pseudo : ${model.username.value}", fontSize = 20.sp)
                    if (model.subscription.value) {
                        Text(text = "Energie : ∞/20⚡", fontSize = 15.sp)
                    } else {
                        if (energy == 20) {
                            Text(text = "Energie : $energy/20⚡", fontSize = 15.sp)
                        } else {
                            Text(text = "Energie : $energy/20⚡ ($timeEnergy)", fontSize = 15.sp)
                        }
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    )
}

@Composable
fun MonBottomBar(model: MenuViewModel, backStack: NavBackStack<NavKey>) {
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
            text = "Des modifications n'ont pas été enregistrées. Voulez-vous les conserver?",
            onConfirmText = "Oui",
            onCancelText = "Non"
        )
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////
/*                                       Navigation Screen                                        */
////////////////////////////////////////////////////////////////////////////////////////////////////

@Composable
fun ProfileScreen(model: MenuViewModel) {
    val username = model.username.value
    val subscription = model.subscription.value
    val endDate = model.endDate.value.split("/")
    val day = LocalDateTime.now().dayOfMonth
    val month = LocalDateTime.now().month.value
    val year = LocalDateTime.now().year

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = model.translateBackground()),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column {
            Text(text = "PROFILE")
            Row {
                Text(text = "Pseudo ")
                UnderlineTextField(value = username) { model.username.value = it }
            }
            if (subscription) {
                val remaining = (endDate[2].toInt() - year) * 12 * 30 + (endDate[1].toInt() - month) * 30 + (endDate[0].toInt() - day)
                Text(text = "Abonné 👑")
                Text(text = "Temps restant : ${remaining}j")
            }
        }
    }
}

@Composable
fun LeaderboardScreen(model: MenuViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = model.translateBackground()),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Text(text = "LEADERBOARD")
    }
}

@Composable
fun HomeScreen(model: MenuViewModel) {
    // todo: load depuis la db
    val difficulty = listOf("Facile", "Moyen", "Difficile")
    val subject = listOf("Maths", "Histoire", "Géographie", "Informatique", "Pokémon")

    val selectedDiff = remember { mutableStateOf(difficulty[0]) }
    val selectedSubj = remember { mutableStateOf(subject[0]) }
    val context = LocalContext.current
    val intent = Intent(context, QuizzActivity::class.java)
    intent.putExtra("difficulty", selectedDiff.value)
    intent.putExtra("subject", selectedSubj.value)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = model.translateBackground()),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column {
            Text(text = "WTF QUIZ")
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
                        Text(text = diff)
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
                    Button(
                        onClick = { selectedSubj.value = subj },
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(8.dp)
                            .fillMaxWidth(),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedSubj.value == subj) Color.Blue else Color.Gray,
                            contentColor = Color.White
                        )
                    ) {
                        Text(subj, textAlign = TextAlign.Center)
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
                    onClick = { model.removeEnergy(); context.startActivity(intent) },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("GOGOGOGOGO")
                }
            }
        }

    }
}

@Composable
fun ShopScreen(model: MenuViewModel) {
    val showAlertMonth = remember { mutableStateOf(false) }
    val showAlertYear = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = model.translateBackground()),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column {
            Text(text = "SHOP")
            Row {
                Button(onClick = { showAlertMonth.value = true }) {
                    Text(text = "1.99€/mois")
                }
                Button(onClick = { showAlertYear.value = true }) {
                    Text(text = "9.99€/an")
                }

                if (showAlertMonth.value) {
                    Alert(
                        onConfirm = { manageSubscription(model = model, isMonthly = true); showAlertMonth.value = false },
                        onCancel = { showAlertMonth.value = false },
                        title = "Confirmer l'abonnement mensuel",
                        text = "L'abonnement sera actif pendant un mois. Pas de renouvelement automatique ni remboursement. \nConfirmez-vous cet achat?",
                        onConfirmText = "Confirmer",
                        onCancelText = "Annuler"
                    )
                }

                if (showAlertYear.value) {
                    Alert(
                        onConfirm = { manageSubscription(model = model, isMonthly = false); showAlertYear.value = false },
                        onCancel = { showAlertYear.value = false },
                        title = "Confirmer l'abonnement annuel",
                        text = "L'abonnement sera actif pendant un an. Pas de renouvelement automatique ni remboursement. \nConfirmez-vous cet achat?",
                        onConfirmText = "Confirmer",
                        onCancelText = "Annuler"
                    )
                }
            }
        }
    }
}

fun manageSubscription(model: MenuViewModel, isMonthly: Boolean) {
    if (model.subscription.value) {
        model.extendSubscription(isMonthly = isMonthly)
    } else {
        model.buySubscription(isMonthly = isMonthly)
    }
}

@Composable
fun SettingsScreen(model: MenuViewModel) {
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
            Text(text = "Paramètre")
            Row {
                Text(text = "Musique")
                SliderManage(value = model.music.floatValue) {
                    model.music.floatValue = it
                    model.saved.value = false
                }
            }
            Row {
                Text(text = "Son")
                SliderManage(value = model.sound.floatValue) {
                    model.sound.floatValue = it
                    model.saved.value = false
                }
            }
            Row {
                Text(text = "Fond")
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
                Text(text = "Police")
                ChoiceField(
                    label = "Police",
                    choice = selectedFont.intValue,
                    onValueChange = { newValue ->
                        selectedFont.intValue = newValue; model.saved.value = false
                    },
                    choiceList = model.fontList
                )
            }
            Row {
                Text(text = "Domaine préféré")
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
            Button(onClick = { update() }) { Text(text = "Enregistrer") }
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
        Text(text = ("%.0f / 100".format(value * 100)))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoiceField(label: String, choice: Int, onValueChange: (Int) -> Unit, choiceList: List<String>) {
    var expanded by remember { mutableStateOf(value = false) }
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
            label = { Text(text = label + expanded) },
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
                    text = { Text(text = c) }
                )
            }
        }
    }
}

@Composable
fun Alert(onConfirm: () -> Unit, onCancel: () -> Unit, title: String, text: String, onConfirmText: String, onCancelText: String) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(text = title) },
        text = { Text(text = text) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = onConfirmText)
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text(text = onCancelText)
            }
        }
    )
}

@Composable
fun UnderlineTextField(value: String, onValueChange: (String) -> Unit) {
    Column {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.Black
        )
    }
}

@Composable
fun DisplayText(text: String, fontSize: TextUnit, font: Int, color: Color) {
}