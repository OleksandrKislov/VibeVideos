package com.pet.shorts.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun BottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val bottomBarVisibilityManager = koinInject<BottomBarVisibilityManager>()
    val isBottomBarVisible by bottomBarVisibilityManager.isBottomBarVisible.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var isAnimationPlaying by remember { mutableStateOf(false) }

    AnimatedVisibility(visible = isBottomBarVisible) {
        NavigationBar(modifier = modifier) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            BottomNavigation.entries.forEach { destination ->
                val isSelected = currentDestination?.hierarchy?.any { it.hasRoute(destination.screen::class) } == true
                NavigationBarItem(
                    selected = isSelected,
                    label = { Text(text = stringResource(id = destination.labelId)) },
                    icon = {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = stringResource(id = destination.labelId)
                        )
                    },
                    onClick = {
                        coroutineScope.launch {
                            if (isBottomBarVisible && !isAnimationPlaying && !isSelected){
                                isAnimationPlaying = true
                                navController.navigate(destination.screen) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                delay(BottomBarVisibilityManager.ANIMATION_DURATION.toLong())
                                isAnimationPlaying = false
                            }
                        }
                    }
                )
            }
        }
    }
}