package fr.uparis.chenliu.projet.ui.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import fr.uparis.chenliu.projet.ui.viewmodel.MenuViewModel

@Composable
fun ShopScreen(model: MenuViewModel) {
    val showAlertMonth = remember { mutableStateOf(false) }
    val showAlertYear = remember { mutableStateOf(false) }
    val currentBackground = model.intToBackground()
    val currentFont = model.intToFont()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = currentBackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DisplayText(list = currentFont, text = "Boutique", fontSize = 24.sp)
            Row {
                Button(onClick = { showAlertMonth.value = true }) {
                    DisplayText(list = currentFont, text = "1.99€/mois")

                }
                Button(onClick = { showAlertYear.value = true }) {
                    DisplayText(list = currentFont, text = "9.99€/an")
                }

                if (showAlertMonth.value) {
                    Alert(
                        list = currentFont,
                        onConfirm = {
                            manageSubscription(
                                model = model,
                                isMonthly = true
                            ); showAlertMonth.value = false
                        },
                        onCancel = { showAlertMonth.value = false },
                        title = "Confirmer l'abonnement mensuel",
                        text = "L'abonnement sera actif pendant un mois. Pas de renouvelement automatique ni remboursement. \nConfirmez-vous cet achat?",
                        onConfirmText = "Confirmer",
                        onCancelText = "Annuler"
                    )
                }

                if (showAlertYear.value) {
                    Alert(
                        list = currentFont,
                        onConfirm = {
                            manageSubscription(
                                model = model,
                                isMonthly = false
                            ); showAlertYear.value = false
                        },
                        onCancel = { showAlertYear.value = false },
                        title = "Confirmer l'abonnement annuel",
                        text = "L'abonnement sera actif pendant un an. Pas de renouvelement automatique ni remboursement. \nConfirmez-vous cet achat?",
                        onConfirmText = "Confirmer",
                        onCancelText = "Annuler"
                    )
                }
            }
        }
    }
}

fun manageSubscription(model: MenuViewModel, isMonthly: Boolean) {
    val isSubscribe = model.subscription.value
    if (isSubscribe) {
        model.extendSubscription(isMonthly = isMonthly)
    } else {
        model.buySubscription(isMonthly = isMonthly)
    }
}