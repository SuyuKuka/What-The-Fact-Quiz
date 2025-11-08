package fr.uparis.chenliu.projet.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Screen: NavKey {

    val icon: ImageVector

    @Serializable
    data object Profile:Screen {
        override val icon = Icons.Filled.Person
    }

    @Serializable
    data object Leaderboard:Screen {
        override val icon = Icons.Filled.Leaderboard
    }

    @Serializable
    data object Home:Screen {
        override val icon = Icons.Filled.Home
    }

    @Serializable
    data object Shop:Screen {
        override val icon = Icons.Filled.ShoppingCart
    }

    @Serializable
    data object Settings: Screen {
        override val icon = Icons.Filled.Settings
    }

    @Serializable
    data object Level: Screen {
        override val icon = Icons.Filled.Air
    }

}