package fr.uparis.chenliu.projet.ui.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
    val currentFont = model.intToFont()
    val backgroundList = model.backgroundList.collectAsState()
    val fontList = model.fontList.collectAsState()
    val context = LocalContext.current
    val saved = model.saved

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = currentBackground.intValue),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DisplayText(list = currentFont, text = "Paramètre", fontSize = 24.sp)
            Row {
                DisplayText(list = currentFont, text = "Musique")
                SliderManage(list = currentFont, value = model.music.floatValue) {
                    model.music.floatValue = it
                    saved.value = false
                }
            }
            Row {
                DisplayText(list = currentFont, text = "Son")
                SliderManage(list = currentFont, value = model.sound.floatValue) {
                    model.sound.floatValue = it
                    saved.value = false
                }
            }
            Row {
                DisplayText(list = model.intToFont(), text = "Fond")
                ChoiceField(
                    model = model,
                    label = "Fond",
                    choice = selectedBackground.intValue,
                    onValueChange = { newValue ->
                        selectedBackground.intValue = newValue; model.updateCurrentBackground(context); saved.value = false
                    },
                    choiceList = backgroundList.value
                )
            }
            Row {
                DisplayText(list = model.intToFont(), text = "Police")
                ChoiceField(
                    model = model,
                    label = "Police",
                    choice = selectedFont.intValue,
                    onValueChange = { newValue ->
                        selectedFont.intValue = newValue; saved.value = false
                    },
                    choiceList = fontList.value
                )
            }
            Row {
                DisplayText(list = model.intToFont(), text = "Domaine préféré")
                WriteField(
                    value = model.preferredTopics.value,
                    onValueChange = {
                        model.preferredTopics.value = it; saved.value = false
                    },
                    label = "Domaine préféré?",
                    modifier = Modifier.padding(start = 10.dp),
                    font = model.intToFont(),
                    fontSize = 18.sp,
                    onDone = { update() },
                    onlyNb = false
                )
            }
            Button(onClick = { update() }) {
                DisplayText(
                    list = model.intToFont(),
                    text = "Enregistrer"
                )
            }
        }
    }
}

@Composable
fun SliderManage(list: List<Int>, value: Float, onValueChange: (Float) -> Unit) {
    Column {
        Slider(
            value = value,
            onValueChange = onValueChange
        )
        DisplayText(list = list, text = ("%.0f / 100".format(value * 100)))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoiceField(model: MenuViewModel, label: String, choice: Int, onValueChange: (Int) -> Unit, choiceList: List<String>) {
    var expanded by remember { mutableStateOf(value = false) }
    val text = choiceList[choice]
    val currectFont = model.intToFont()
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = text,
            onValueChange = { },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                disabledContainerColor = Color.White
            ),
            label = { DisplayText(list = currectFont, text = label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = (16 - currectFont[1]).sp,
                fontFamily = FontFamily(Font(currectFont[0]))
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            choiceList.forEachIndexed { index, c ->
                DropdownMenuItem(
                    onClick = {
                        onValueChange(index)
                        expanded = false
                    },
                    text = { DisplayText(list = currectFont, text = c) }
                )
            }
        }
    }
}