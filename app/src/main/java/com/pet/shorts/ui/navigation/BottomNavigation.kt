package com.pet.shorts.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.pet.shorts.R

enum class BottomNavigation(val labelId: Int, val icon: ImageVector, val screen: Screen) {
    HomeScreenDestination(
        labelId = R.string.home,
        icon = Icons.Default.Home,
        screen = Screen.Home
    ),
    FavoriteScreenDestination(
        labelId = R.string.favorite,
        icon = Icons.Default.Favorite,
        screen = Screen.Favorite
    )
}

