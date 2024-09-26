package com.pet.shorts.ui.screen.favorite

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import com.pet.shorts.R
import com.pet.shorts.ui.components.VideoPlayer
import com.pet.shorts.ui.components.modifiers.holdable
import com.pet.shorts.ui.navigation.BottomBarVisibilityManager
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FavoriteScreen(
    state: State<FavoriteViewModel.ScreenState>,
    onEvent: (FavoriteViewModel.UiEvent) -> Unit,
) {
    val context = LocalContext.current
    val imageLoader = LocalContext.current.imageLoader
    val videosLazyPagingItems = state.value.videosList.collectAsLazyPagingItems()
    val bottomBarVisibilityManager = koinInject<BottomBarVisibilityManager>()
    val lazyGridState =  rememberLazyGridState()
    SharedTransitionLayout {
        AnimatedContent(
            targetState = state.value.videoToShow == null,
            label = "favorite screen"
        ) { targetState ->
            if (targetState) {
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp),
                    state = lazyGridState,
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.Top),
                    horizontalArrangement = Arrangement.spacedBy(
                        15.dp,
                        Alignment.CenterHorizontally
                    ),
                ) {
                    items(
                        count = videosLazyPagingItems.itemCount,
                        key = videosLazyPagingItems.itemKey { it.id },
                        contentType = videosLazyPagingItems.itemContentType { "Video" }
                    ) { index ->
                        val video = videosLazyPagingItems[index]
                        if (video != null) {
                            var imageToShow by remember { mutableStateOf(video.pictures.firstOrNull()) }
                            AsyncImage(
                                modifier = Modifier
                                    .holdable(
                                        onClick = {
                                            onEvent(FavoriteViewModel.UiEvent.OpenVideo(video))
                                        },
                                        onHold = {
                                            video.pictures
                                                .map { url ->
                                                    async {
                                                        val request = ImageRequest
                                                            .Builder(context)
                                                            .data(url)
                                                            .build()
                                                        imageLoader.execute(request)
                                                    }
                                                }
                                                .forEachIndexed { index, item ->
                                                    item.await()
                                                    imageToShow = video.pictures[index]
                                                    delay(300)
                                                }

                                            while (isActive)
                                                video.pictures.forEach {
                                                    delay(300)
                                                    imageToShow = it
                                                }
                                        }
                                    )
                                    .sharedBounds(
                                        rememberSharedContentState(key = video.id),
                                        animatedVisibilityScope = this@AnimatedContent
                                    )
                                    .fillMaxSize()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(15.dp)),
                                model = imageToShow,
                                contentDescription = stringResource(R.string.video_preview, index),
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .sharedBounds(
                            rememberSharedContentState(key = state.value.lastShowedVideoId),
                            animatedVisibilityScope = this@AnimatedContent,
                        )
                        .fillMaxSize()
                ) {
                    state.value.videoToShow?.let { video ->
                        bottomBarVisibilityManager.hideBottomBar()
                        val exoPlayer: ExoPlayer = koinInject(parameters = { parametersOf(video.url) })
                        LaunchedEffect(Unit) {
                            onEvent(FavoriteViewModel.UiEvent.SubscribeIsFavorite(video.id))
                        }
                        VideoPlayer(
                            modifier = Modifier.fillMaxSize(),
                            exoPlayer = exoPlayer,
                            isVideoPlaying = state.value.isVideoPlaying,
                            videoUrl = video.url,
                            isFavorite = state.value.isVideoToShowFavorite ?: true,
                            backgroundImageUrl = video.pictures.firstOrNull(),
                            toggleFavorite = {
                                onEvent(FavoriteViewModel.UiEvent.ToggleFavorite(video))
                            },
                            toggleVideoPlaying = {
                                onEvent(FavoriteViewModel.UiEvent.SetIsVideoPlaying(it))
                            }
                        )
                    }
                }
                BackHandler(state.value.videoToShow != null) {
                    onEvent(FavoriteViewModel.UiEvent.CloseVideo)
                    bottomBarVisibilityManager.showBottomBar()
                }
            }
        }
    }
}