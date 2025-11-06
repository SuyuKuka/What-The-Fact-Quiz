package fr.uparis.chenliu.projet.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uparis.chenliu.projet.ui.theme.ProjetTheme
import fr.uparis.chenliu.projet.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjetTheme {
                MyScreen()
            }
        }
    }
}

@Composable
fun MyScreen(model: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val intent = Intent(context, MainMenuActivity::class.java)
    var username = model.username.value
    val loading = model.loading.value
    val exist = model.exist.value
    val continueAction = { model.insertUsers(); intent.putExtra("username", username);context.startActivity(intent) }

    if (!loading && exist) {
        intent.putExtra("username", username)
        context.startActivity(intent)
    }

    Column {
        Text(text = "WTF (What The Fact) Quiz")
        WriteField(username, { model.username.value = it }, "Entrer votre pseudo", modifier = Modifier.padding(start = 10.dp), 25.sp, continueAction)
        Button(onClick = continueAction ) { Text("Continuer") }
        Button(onClick = { model.deleteAllUsers() }) { Text("Supprimer les données") }
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