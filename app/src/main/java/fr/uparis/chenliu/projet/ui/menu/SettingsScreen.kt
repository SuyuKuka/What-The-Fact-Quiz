package fr.uparis.chenliu.projet.ui.menu

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.uparis.chenliu.projet.ui.WriteField
import fr.uparis.chenliu.projet.ui.viewmodel.MenuViewModel

@Composable
fun SettingsScreen(model: MenuViewModel) {
    val update = { model.updateSettings(); model.saved.value = true }
    val selectedBackground = model.background
    val selectedFont = model.font
    val currentBackground = model.currentBackground
    val currentFont = model.currentFont
    val backgroundList = model.backgroundList.collectAsState()
    val fontList = model.fontList.collectAsState()
    val context = LocalContext.current
    val saved = model.saved

    Box(modifier = Modifier.fillMaxSize()) {
        DisplayBackground(currentBackground = currentBackground.intValue)

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            SettingsTitle(font = currentFont.value)
            SettingsBackground(
                model = model,
                selectedBackground = selectedBackground,
                backgroundList = backgroundList.value,
                font = currentFont.value,
                context = context,
                saved = saved
            )
            SettingsFont(
                model = model,
                selectedFont = selectedFont,
                fontList = fontList.value,
                font = currentFont.value,
                context = context,
                saved = saved
            )
            SettingsPreferredTopics(model = model, font = currentFont.value, update = update, saved = saved)
            SettingsButton(font = currentFont.value, update = update)
        }
    }
}

@Composable
fun SettingsTitle(font: List<Int>) {
    Spacer(modifier = Modifier.height(24.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        DisplayText(
            text = "Paramètre",
            font = font,
            fontSize = 30.sp
        )
    }
}


@Composable
fun SettingsBackground(model: MenuViewModel, selectedBackground: MutableState<Int>, backgroundList: List<String>, font: List<Int>, context: Context, saved: MutableState<Boolean>) {

    DecoCard(
        title = "Fond d'écran",
        font = font,
    ) {
        ChoiceField(
            model = model,
            label = "",
            choice = selectedBackground.value,
            onValueChange = { newValue ->
                selectedBackground.value = newValue; model.updateCurrentBackground(context); saved.value = false
            },
            choiceList = backgroundList
        )
    }
}

@Composable
fun SettingsFont(model: MenuViewModel, selectedFont: MutableState<Int>, fontList: List<String>, font: List<Int>, context: Context, saved: MutableState<Boolean>) {
    DecoCard(
        title = "Police d'écriture",
        font = font,
    ) {
        ChoiceField(
            model = model,
            label = "",
            choice = selectedFont.value,
            onValueChange = { newValue ->
                selectedFont.value = newValue; model.updateCurrentFont(context); saved.value =
                false
            },
            choiceList = fontList
        )
    }
}

@Composable
fun SettingsPreferredTopics(model: MenuViewModel, font: List<Int>, update: () -> Unit, saved: MutableState<Boolean>) {
    DecoCard(
        title = "Domaine préféré",
        font = font,
    ) {
        WriteField(
            value = model.preferredTopics.value,
            onValueChange = {
                model.preferredTopics.value = it; saved.value = false
            },
            label = "",
            modifier = Modifier.padding(start = 10.dp),
            font = font,
            fontSize = 18.sp,
            onDone = { update() },
            onlyNb = false
        )
    }
}

@Composable
fun SettingsButton(font: List<Int>, update: () -> Unit) {
    val context = LocalContext.current

    Button(onClick = {
        update()
        Toast.makeText(context, "Paramètres enregistrés", Toast.LENGTH_SHORT).show()
    }) {
        DisplayText(
            text = "Enregistrer",
            font = font
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoiceField(model: MenuViewModel, label: String, choice: Int, onValueChange: (Int) -> Unit, choiceList: List<String>) {
    var expanded by remember { mutableStateOf(value = false) }
    val text = choiceList[choice]
    val currentFont = model.currentFont
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color.Black, RoundedCornerShape(12.dp)),
            readOnly = true,
            value = text,
            onValueChange = { },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            label = { DisplayText(text = label, font = currentFont.value) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = (16 - currentFont.value[1]).sp,
                fontFamily = FontFamily(Font(currentFont.value[0]))
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Transparent),
            containerColor = Color.White
        ) {
            choiceList.forEachIndexed { index, selectedChoice ->
                DropdownMenuItem(
                    text = {
                        DisplayText(
                            text = selectedChoice,
                            font = currentFont.value
                        )
                    },
                    onClick = {
                        onValueChange(index)
                        expanded = false
                    }
                )
            }
        }
    }
}