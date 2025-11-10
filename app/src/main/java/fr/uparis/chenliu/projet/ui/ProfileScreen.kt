package fr.uparis.chenliu.projet.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.uparis.chenliu.projet.ui.viewmodel.MenuViewModel
import java.time.LocalDateTime

@Composable
fun ProfileScreen(model: MenuViewModel) {
    val username = model.username.value
    val subscription = model.subscription.value
    val preferredTopics = model.preferredTopics.value
    val endDate = model.endDate.value.split("/")
    val day = LocalDateTime.now().dayOfMonth
    val month = LocalDateTime.now().month.value
    val year = LocalDateTime.now().year
    val currentBackground = model.intToBackground()
    val currentFont = model.intToFont()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = currentBackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DisplayText(
                list = currentFont,
                text = "PROFILE",
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
            Row {
                DisplayText(list = currentFont, text = "Pseudo ")
                UnderlineTextField(
                    list = currentFont,
                    value = username,
                    onValueChange = { model.username.value = it; model.saved.value = false },
                    onDone = { model.updateUsername(); model.saved.value = true }
                )
            }
            if (subscription) {
                val remaining =
                    (endDate[2].toInt() - year) * 12 * 30 + (endDate[1].toInt() - month) * 30 + (endDate[0].toInt() - day)
                DisplayText(
                    list = currentFont,
                    text = "Abonné 👑",
                    textAlign = TextAlign.Center
                )
                DisplayText(
                    list = currentFont,
                    text = "Temps restant : ${remaining}j",
                    textAlign = TextAlign.Center
                )
            }
            if (preferredTopics != "") {
                DisplayText(
                    list = currentFont,
                    text = preferredTopics,
                    textAlign = TextAlign.Center
                )
            }
            Button(onClick = { model.updateUsername(); model.saved.value = true }
            ) { DisplayText(list = currentFont, text = "Enregistrer") }
        }
    }
}

@Composable
fun UnderlineTextField(list: List<Int>, value: String, onValueChange: (String) -> Unit, onDone: () -> Unit) {
    Column {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontSize = (16 - list[1]).sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(list[0]))
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onDone() })
        )
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.Black
        )
    }
}