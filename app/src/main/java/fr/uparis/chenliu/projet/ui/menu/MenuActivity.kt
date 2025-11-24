package fr.uparis.chenliu.projet.ui.menu

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
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
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import fr.uparis.chenliu.projet.data.store.TimeConfig
import fr.uparis.chenliu.projet.navigation.Screen
import fr.uparis.chenliu.projet.ui.theme.ProjetTheme
import fr.uparis.chenliu.projet.ui.viewmodel.MenuViewModel
import fr.uparis.chenliu.projet.ui.viewmodel.TimeConfigViewModel
import fr.uparis.chenliu.projet.ui.worker.WorkerReminder
import java.util.concurrent.TimeUnit

const val CHANNEL_ID = "mon_canal"

class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // cacher les barres systems (mode immerssif)
        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        createChannel(context = this)
        setContent {
            ProjetTheme {
                val username = intent.getStringExtra("username")!!
                if (username == "") {
                    finish()
                }
                MyScreenMenu(username = username)
            }
        }
    }
}

@Composable
fun MyScreenMenu(username: String, model: MenuViewModel = viewModel()) {
    val backStack = rememberNavBackStack(Screen.Home)
    val snack = remember { SnackbarHostState() }

    model.init(username)
    DailyRemind()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBarMenu(model = model, backStack = backStack) },
        bottomBar = { BottomBar(model = model, backStack = backStack) },
        snackbarHost = { SnackbarHost(hostState = snack) }
    )
    { pad ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(paddingValues = pad),
            entryProvider = entryProvider{
                entry<Screen.Profile> { model.getUsers(); ProfileScreen(model = model) }
                entry<Screen.Leaderboard> { model.changeSubject("Science"); model.changed.value = true; LeaderboardScreen(
                    model = model
                )
                }
                entry<Screen.Home> { HomeScreen(model = model, backStack = backStack) }
                entry<Screen.Shop> { ShopScreen(model = model) }
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
    textAlign: TextAlign = TextAlign.Start,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = (fontSize.value - list[1]).sp,
        color = color,
        fontFamily = FontFamily(Font(list[0])),
        textAlign = textAlign,
        modifier = modifier
    )
}

fun createChannel(context: Context) {
    val channel = NotificationChannel(CHANNEL_ID, "Mon Canal", NotificationManager.IMPORTANCE_DEFAULT)
    channel.description = "Mon canal de notifications"
    val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}

fun gainEnergyRemind(context: Context, remainingTime: Long) {
    val req = OneTimeWorkRequestBuilder<WorkerReminder>().setInitialDelay(remainingTime, TimeUnit.SECONDS).addTag("GAIN_ENERGY_REMINDER").build()
    WorkManager.getInstance(context).enqueue(req)
}

fun fullEnergyRemind(context: Context, currentEnergy: Int, remainingTime: Long) {
    val maxEnergy = 20
    val remainingSeconds = ((maxEnergy - 1) - currentEnergy) * 122L
    val time = remainingTime + remainingSeconds

    val notificationData = Data.Builder().putString("NOTIFICATION_TYPE", "FULL_ENERGY").build()
    val req = OneTimeWorkRequestBuilder<WorkerReminder>().setInitialDelay(time, TimeUnit.SECONDS).setInputData(notificationData).build()
    WorkManager.getInstance(context).beginUniqueWork("FULL_ENERGY_REMINDER", ExistingWorkPolicy.REPLACE, req).enqueue()
}

@Composable
private fun DailyRemind(model: TimeConfigViewModel = viewModel()) {
    val config = TimeConfig(hMorning = 8, mMorning = 0)
    model.save(config)
    model.schedule(config)
}