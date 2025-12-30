package fr.uparis.chenliu.projet.ui.menu

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
    val currentBackground = model.currentBackground
    val currentFont = model.currentFont

    Box(modifier = Modifier.fillMaxSize()) {
        DisplayBackground(currentBackground = currentBackground.intValue)
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileTitle(font = currentFont.value)
            ProfileUsername(model = model, username = username, font = currentFont.value)
            ProfileSubscription(model = model, subscription = subscription, font = currentFont.value)
            ProfilePreferredTopics(preferredTopics = preferredTopics, font = currentFont.value)
            ProfileButton(model = model, font = currentFont.value)
        }
    }
}

@Composable
fun ProfileTitle(font: List<Int>) {
    DisplayText(text = "Profile", font = font, fontSize = 24.sp)
}

@Composable
fun ProfileUsername(model: MenuViewModel, username: String, font: List<Int>) {
    Row {
        DisplayText(text = "Pseudo", font = font)
        UnderlineTextField(
            list = font,
            value = username,
            onValueChange = { if (it.length <= 16) model.username.value = it; model.saved.value = false },
            onDone = { model.updateUsername(); model.saved.value = true }
        )
    }
}

@Composable
fun ProfileSubscription(model: MenuViewModel, subscription: Boolean, font: List<Int>) {
    val endDate = model.endDate.value.split("/")
    val day = LocalDateTime.now().dayOfMonth
    val month = LocalDateTime.now().month.value
    val year = LocalDateTime.now().year
    if (subscription) {
        val remaining =
            (endDate[2].toInt() - year) * 12 * 30 + (endDate[1].toInt() - month) * 30 + (endDate[0].toInt() - day)
        DisplayText(
            text = "Abonné 👑",
            font = font,
            textAlign = TextAlign.Center
        )
        DisplayText(
            text = "Temps restant : ${remaining}j",
            font = font,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ProfilePreferredTopics(preferredTopics: String, font: List<Int>) {
    if (preferredTopics != "") {
        DisplayText(
            text = preferredTopics,
            font = font,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ProfileButton(model: MenuViewModel, font: List<Int>) {
    Button(
        onClick = { model.updateUsername(); model.saved.value = true }
    ) {
        DisplayText(text = "Enregistrer", font = font)
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