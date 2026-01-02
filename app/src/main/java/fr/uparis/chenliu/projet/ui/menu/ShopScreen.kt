package fr.uparis.chenliu.projet.ui.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.uparis.chenliu.projet.ui.viewmodel.MenuViewModel

@Composable
fun ShopScreen(model: MenuViewModel) {
    val showAlertMonth = remember { mutableStateOf(false) }
    val showAlertYear = remember { mutableStateOf(false) }
    val currentBackground = model.currentBackground
    val currentFont = model.currentFont

    Box(modifier = Modifier.fillMaxSize()) {
        DisplayBackground(currentBackground = currentBackground.intValue)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            ShopTitle(font = currentFont.value)
            ShopDesc(font = currentFont.value)
            ShopSubscriptionM(
                font = currentFont.value,
                showAlertMonth = showAlertMonth,
            )
            ShopSubscriptionY(
                font = currentFont.value,
                showAlertYear = showAlertYear
            )
            ShopAlert(
                model = model,
                font = currentFont.value,
                showAlertMonth = showAlertMonth,
                showAlertYear = showAlertYear
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
                color = MaterialTheme.colorScheme.secondary,
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
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        DisplayText(
            text = "Profitez de l’Énergie Illimitée et de toutes les fonctionnalités !",
            font = font,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ShopSubscriptionM(font: List<Int>, showAlertMonth: MutableState<Boolean>) {
    DecoCard(
        title = "Abonnement mensuel",
        title2 = "1.99€ pour 1 mois",
        font = font
    ) {
        Row {
            Button(
                onClick = { showAlertMonth.value = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                DisplayText(
                    text = "Acheter",
                    font = font,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun ShopSubscriptionY(font: List<Int>, showAlertYear: MutableState<Boolean>) {
    DecoCard(
        title = "Abonnement annuel",
        title2 = "9.99€ pour 1 an",
        font = font
    ) {
        Row {
            Button(
                onClick = { showAlertYear.value = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                DisplayText(
                    text = "Acheter",
                    font = font,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun ShopAlert(model: MenuViewModel, font: List<Int>, showAlertMonth: MutableState<Boolean>, showAlertYear: MutableState<Boolean>) {
    val titleAlert =
        if (showAlertMonth.value) {
            "Confirmer l'abonnement mensuel"
        } else {
            "Confirmer l'abonnement annuel"
        }
    val textAlert =
        if (showAlertMonth.value) {
            "L'abonnement sera actif pendant un mois. Pas de renouvelement automatique ni remboursement. \nConfirmez-vous cet achat?"
        } else {
            "L'abonnement sera actif pendant un an. Pas de renouvelement automatique ni remboursement. \nConfirmez-vous cet achat?"
        }
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