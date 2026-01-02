package fr.uparis.chenliu.projet.ui

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uparis.chenliu.projet.R
import fr.uparis.chenliu.projet.ui.menu.MenuActivity
import fr.uparis.chenliu.projet.ui.theme.ProjetTheme
import fr.uparis.chenliu.projet.ui.viewmodel.MainViewModel
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjetTheme {
                val permission = android.Manifest.permission.POST_NOTIFICATIONS
                val permissionCheckResult = ContextCompat.checkSelfPermission(this, permission)
                if (permissionCheckResult != PackageManager.PERMISSION_GRANTED) {
                    if (!shouldShowRequestPermissionRationale(permission)) {
                        requestPermissions(arrayOf(permission), 0)
                    }
                }
                HideSystemBar(window)
                MainScreen(activity = this)
            }
        }
    }
}

@Composable
fun MainScreen(activity: MainActivity, model: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val intent = Intent(context, MenuActivity::class.java)
    val username = model.username.value
    val loading = model.loading.value
    val exist = model.exist.value
    val animation = ActivityOptions.makeCustomAnimation(
        context,
        R.anim.fade_in,
        R.anim.fade_out
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_default_blurred),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (loading) {
            LoadingScreen()
        } else {
            if (exist) {
                if (model.endDate.value != "") {
                    checkSubscriptionDate(model = model)
                }
                intent.putExtra("username", username)
                context.startActivity(intent, animation.toBundle())
                activity.finish()
            } else {
                MainElement(model = model, username = username, context = context)
            }
        }
    }
}

fun checkSubscriptionDate(model: MainViewModel) {
    val endDate = model.endDate.value.split("/")
    val dayToday = LocalDateTime.now().dayOfMonth
    val monthToday = LocalDateTime.now().month.value
    val yearToday = LocalDateTime.now().year
    if (endDate[2].toInt() <= yearToday) {
        if (endDate[1].toInt() < monthToday) {
            if (endDate[0].toInt() <= dayToday) {
                model.expireSubscription()
            }
        }
    }
}

@Composable
fun MainElement(model: MainViewModel, username: String, context: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainTitle(
            text = "What The Fact",
            fontSize = 40.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
        )
        MainTitle(
            text = "Quiz",
            fontSize = 60.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).graphicsLayer(rotationZ = 15f)
        )
        Spacer(modifier = Modifier.height(80.dp))
        MainContent(model = model, username = username, context = context)
        Spacer(modifier = Modifier.height(45.dp))
        MainButton(model = model, context = context)
    }
}

@Composable
fun MainTitle(text: String, fontSize: TextUnit, textAlign: TextAlign, modifier: Modifier) {
    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        textAlign = textAlign,
        color = Color.White,
        style = TextStyle(
            shadow = Shadow(
                color = Color.Black,
                offset = Offset(4f, 4f),
                blurRadius = 8f
            ),
            textDecoration = TextDecoration.Underline
        ),
        modifier = modifier
    )
}

@Composable
fun MainContent(model: MainViewModel, username: String, context: Context) {
    Text(
        text = "Entrer votre pseudo",
        modifier = Modifier
            .background(
                Color.White,
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
    WriteField(
        value = username,
        onValueChange = { if (it.length <= 16) model.username.value = it },
        label = "",
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        fontSize = 25.sp,
        onDone = { model.insertData(context) },
    )
}

@Composable
fun MainButton(model: MainViewModel, context: Context) {
    Button(
        onClick = { model.insertData(context) },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(243, 205, 26),
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = if (model.username.value == "") {
                "Peut-être plus tard"
            } else {
                "Continuer"
            }
        )
    }
}

@Composable
fun WriteField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier,
    font: List<Int> = listOf(0, 0),
    fontSize: TextUnit,
    onDone: () -> Unit,
    onlyNb: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black,
            focusedLabelColor = Color.Black,
            unfocusedLabelColor = Color.Black,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        label = {
            Text(
                text = label,
                fontSize = (fontSize.value - font[1]).sp,
                fontFamily = FontFamily(Font(font[0])),
                color = Color.Black
            )
        },
        keyboardOptions =
            if (onlyNb) {
                KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
            } else {
                KeyboardOptions(imeAction = ImeAction.Done)
            },
        modifier = modifier,
        keyboardActions = KeyboardActions(onDone = { onDone() })
    )
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_loading),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(weight = 3f))
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(size = 70.dp)
            )
            Text(
                text = "Chargement...",
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 20.dp)
            )
            Spacer(modifier = Modifier.weight(weight = 0.5f))
        }
    }
}

@Composable
fun HideSystemBar(window: Window) {
    val windowInsetsController =
        WindowCompat.getInsetsController(window, window.decorView)
    windowInsetsController.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
}