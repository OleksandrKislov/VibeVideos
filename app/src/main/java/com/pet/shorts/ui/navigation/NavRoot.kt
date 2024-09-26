package com.pet.shorts.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pet.shorts.ui.screen.favorite.FavoriteScreen
import com.pet.shorts.ui.screen.favorite.FavoriteViewModel
import com.pet.shorts.ui.screen.home.HomeScreen
import com.pet.shorts.ui.screen.home.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavRoot(
    modifier: Modifier,
    navController: NavHostController
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.Home,
    ) {
        composable<Screen.Home>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    tween(BottomBarVisibilityManager.ANIMATION_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    tween(BottomBarVisibilityManager.ANIMATION_DURATION)
                )
            },
        ) {
            val viewModel: HomeViewModel = koinViewModel()
            HomeScreen(
                state = viewModel.state.collectAsStateWithLifecycle(),
                onEvent = viewModel::onEvent
            )
        }

        composable<Screen.Favorite>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    tween(BottomBarVisibilityManager.ANIMATION_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    tween(BottomBarVisibilityManager.ANIMATION_DURATION)
                )
            },
        ) {
            val viewModel: FavoriteViewModel = koinViewModel()
            FavoriteScreen(
                state = viewModel.state.collectAsStateWithLifecycle(),
                onEvent = viewModel::onEvent
            )
        }
    }
}