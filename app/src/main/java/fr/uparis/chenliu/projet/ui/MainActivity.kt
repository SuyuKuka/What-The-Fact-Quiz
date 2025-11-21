package fr.uparis.chenliu.projet.ui

import android.R.attr.top
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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
import java.time.format.TextStyle

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // cacher les barres systems (mode immerssif)
        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            ProjetTheme {
                val permission = android.Manifest.permission.POST_NOTIFICATIONS
                val permissionCheckResult = ContextCompat.checkSelfPermission(this, permission)
                if (permissionCheckResult != PackageManager.PERMISSION_GRANTED) {
                    if (!shouldShowRequestPermissionRationale(permission)) {
                        requestPermissions(
                            arrayOf(permission),
                            0
                        )
                    }
                }
                MyScreen(activity = this)
            }
        }
    }
}

@Composable
fun MyScreen(activity: MainActivity, model: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val intent = Intent(context, MenuActivity::class.java)
    val username = model.username.value
    val loading = model.loading.value
    val exist = model.exist.value

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
            }
            intent.putExtra("username", username)
            context.startActivity(intent)
            activity.finish()
        } else {
            Column {
                Text(text = "WTF (What The Fact) Quiz")
                WriteField(
                    value = username,
                    onValueChange = { model.username.value = it },
                    label = "Entrer votre pseudo",
                    modifier = Modifier.padding(start = 10.dp),
                    font = listOf(-1, 0),
                    onDone = { model.insertData(context) }
                )
                context.startActivity(intent, options.toBundle())

                activity.finish()


            } else {
                Image(
                    painter = painterResource(id = R.drawable.background_defaut),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 80.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "What The Fact",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                        color = Color.White,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(4f, 4f),
                                blurRadius = 8f
                            ),
                            textDecoration = TextDecoration.Underline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )
                    Text(
                        text = "Quiz",
                        fontSize = 60.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        color = Color.White,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(4f, 4f),
                                blurRadius = 8f
                            ),
                            textDecoration = TextDecoration.Underline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .graphicsLayer(rotationZ = 15f)
                    )
                    Spacer(modifier = Modifier.height(80.dp))
                    Text(
                        "⬇\uFE0F Entrer votre pseudo ⬇\uFE0F",
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
                        onValueChange = { model.username.value = it },
                        label = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        fontSize = 25.sp,
                        onDone = { model.insertData(context) },

                        )
                    Spacer(modifier = Modifier.height(45.dp))
                    Button(
                        onClick = { model.insertData(context) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
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
            }
        }
    }
}

@Composable
fun WriteField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier,
    font: List<Int>,
    fontSize: TextUnit = 16.sp,
    onDone: () -> Unit,
    onlyNb: Boolean = false,
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
        singleLine = true,
        label = {
            Text(
                text = label,
                fontSize = (fontSize.value - font[1]).sp,
                fontFamily = if (font[0] == -1) {
                    FontFamily(Font(font[0]))
                } else {
                    FontFamily.Default
                }
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