package fr.uparis.chenliu.projet.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uparis.chenliu.projet.R
import fr.uparis.chenliu.projet.ui.theme.ProjetTheme
import fr.uparis.chenliu.projet.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjetTheme {
                MyScreen(this)
            }
        }
    }
}

@Composable
fun MyScreen(activity: MainActivity, model: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val intent = Intent(context, MainMenuActivity::class.java)
    val username = model.username.value
    val loading = model.loading.value
    val exist = model.exist.value

    if (loading) {
        LoadingScreen()
    } else {
        if (exist) {

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
                    fontSize = 25.sp,
                    onDone = { model.insertUsers() }
                )
                Button(onClick = { model.initQuizz(context); model.insertUsers() } ) { Text("Continuer") }
                Button(onClick = { model.deleteAllUsers() }) { Text("Supprimer les données") }
            }
        }
    }
}

@Composable
fun WriteField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier, fontSize: TextUnit, onDone: () -> Unit, onlyNb: Boolean = false) {
    OutlinedTextField(
        value, onValueChange = onValueChange,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Black,
            focusedLabelColor = Color.Black,
            unfocusedLabelColor = Color.Black
        ),
        singleLine = true,
        label = { Text(text = label, fontSize = fontSize) },
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
            Spacer(modifier = Modifier.weight(3f))
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier
                    .size(70.dp)
            )
            Text(
                "Chargement...",
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 20.dp)
            )
            Spacer(modifier = Modifier.weight(0.5f))
        }
    }
}