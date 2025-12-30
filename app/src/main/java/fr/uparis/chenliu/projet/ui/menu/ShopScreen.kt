package fr.uparis.chenliu.projet.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.uparis.chenliu.projet.ui.viewmodel.MenuViewModel

@Composable
fun ShopScreen(model: MenuViewModel) {
    val showAlertMonth = remember { mutableStateOf(false) }
    val showAlertYear = remember { mutableStateOf(false) }
    val currentBackground = model.currentBackground
    val currentFont = model.currentFont
    val titleAlert = remember {
        if (showAlertMonth.value) {
            mutableStateOf("Confirmer l'abonnement mensuel")
        } else {
            mutableStateOf("Confirmer l'abonnement annuel")
        }
    }
    val textAlert = remember {
        if (showAlertMonth.value) {
            mutableStateOf("L'abonnement sera actif pendant un mois. Pas de renouvelement automatique ni remboursement. \nConfirmez-vous cet achat?")
        } else {
            mutableStateOf("L'abonnement sera actif pendant un an. Pas de renouvelement automatique ni remboursement. \nConfirmez-vous cet achat?")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        DisplayBackground(currentBackground = currentBackground.intValue)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            ShopTitle(font = currentFont.value)
            ShopDesc(font = currentFont.value)
            ShopSubscription(
                font = currentFont.value,
                showAlertMonth = showAlertMonth,
                showAlertYear = showAlertYear
            )
            ShopAlert(
                model = model,
                font = currentFont.value,
                showAlertMonth = showAlertMonth,
                showAlertYear = showAlertYear,
                titleAlert = titleAlert.value,
                textAlert = textAlert.value
            )
        }
    }
}

@Composable
fun ShopTitle(font: List<Int>) {
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
            text = "Boutique",
            font = font,
            fontSize = 30.sp
        )
    }
}

@Composable
fun ShopDesc(font: List<Int>) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        DisplayText(
            text = "Profitez de l’Énergie Illimitée et de toutes les fonctionnalités !",
            font = font,
            fontSize = 20.sp
        )
    }
}

@Composable
fun ShopSubscription(font: List<Int>, showAlertMonth: MutableState<Boolean>, showAlertYear: MutableState<Boolean>) {
    Row {
        Button(onClick = { showAlertMonth.value = true }) {
            DisplayText(text = "1.99€/mois", font = font)
        }
        Button(onClick = { showAlertYear.value = true }) {
            DisplayText(text = "9.99€/an", font = font)
        }
    }
}

@Composable
fun ShopAlert(model: MenuViewModel, font: List<Int>, showAlertMonth: MutableState<Boolean>, showAlertYear: MutableState<Boolean>, titleAlert: String, textAlert: String) {
    if (showAlertMonth.value || showAlertYear.value) {
        Alert(
            font = font,
            onConfirm = {
                manageSubscription(
                    model = model,
                    isMonthly = showAlertMonth.value
                )
                showAlertMonth.value = false
                showAlertYear.value = false
            },
            onCancel = { showAlertMonth.value = false; showAlertYear.value = false },
            title = titleAlert,
            text = textAlert,
            onConfirmText = "Confirmer",
            onCancelText = "Annuler"
        )
    }
}

fun manageSubscription(model: MenuViewModel, isMonthly: Boolean) {
    val isSubscribe = model.subscription.value
    if (isSubscribe) {
        model.manageSubscription(isMonthly = isMonthly, isBuying = false)
    } else {
        model.manageSubscription(isMonthly = isMonthly, isBuying = true)
    }
}